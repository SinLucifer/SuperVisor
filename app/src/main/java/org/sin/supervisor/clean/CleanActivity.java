package org.sin.supervisor.clean;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.sin.supervisor.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Sin on 2015/10/6.
 */
public class CleanActivity extends ListActivity {
    private PackageManager pm;
    private ListView lv;
    private MyAdapter adapter;
    private Map<String, CacheInfo> maps;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cache_clear);
        pm = getPackageManager();
        maps = new HashMap<String, CacheInfo>();
        lv = getListView();
        List<PackageInfo> packageinfos = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);

        for (PackageInfo info : packageinfos) {
            String name = info.applicationInfo.loadLabel(pm).toString();
            String packname = info.packageName;
            Drawable icon = info.applicationInfo.loadIcon(pm);
            CacheInfo cacheinfo = new CacheInfo();
            cacheinfo.setName(name);
            cacheinfo.setPackageName(packname);
            cacheinfo.setIcon(icon);
            maps.put(packname, cacheinfo);
            getAppSize(packname);
        }


        adapter = new MyAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent();
                intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.addCategory("android.intent.category.DEFAULT");
                CacheInfo info = (CacheInfo) lv.getItemAtPosition(position);
                intent.setData(Uri.parse("package:" + info.getPackageName()));
                startActivity(intent);
            }
        });
    }

    private void getAppSize(final String packname) {
        try {
            Method method = PackageManager.class.getMethod(
                    "getPackageSizeInfo", new Class[] { String.class,
                            IPackageStatsObserver.class });
            method.invoke(pm, new Object[] { packname,
                    new IPackageStatsObserver.Stub() {
                        public void onGetStatsCompleted(PackageStats pStats,
                                                        boolean succeeded) throws RemoteException {
                            long cachesize = pStats.cacheSize;
                            long codesize = pStats.codeSize;
                            long datasize = pStats.dataSize;
                            CacheInfo info = maps.get(packname);
                            info.setCacheSize(TextFormater
                                    .getSizeFormat(cachesize));
                            info.setDataSize(TextFormater
                                    .getSizeFormat(datasize));
                            info.setCodeSize(TextFormater
                                    .getSizeFormat(codesize));
                            maps.put(packname, info);
                        }
                    } });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyAdapter extends BaseAdapter {

        private Set<Map.Entry<String, CacheInfo>> sets;
        private List<CacheInfo> cacheinfos;

        public MyAdapter() {
            sets = maps.entrySet();
            cacheinfos = new ArrayList<CacheInfo>();

            for (Map.Entry<String, CacheInfo> entry : sets) {
                cacheinfos.add(entry.getValue());
            }


            try {
                Collections.sort(cacheinfos, new Comparator<CacheInfo>() {


                    @Override
                    public int compare(CacheInfo lhs, CacheInfo rhs) {
                        int ltemp = 0;
                        int rtemp = 0;

                        if (lhs.getCacheSize() != null || rhs.getCacheSize() != null) {
                            String regEx = "[^0-9]";
                            Pattern p = Pattern.compile(regEx);
                            Matcher l = p.matcher(lhs.getCacheSize());
                            Matcher r = p.matcher(rhs.getCacheSize());
                            ltemp = Integer.parseInt(l.replaceAll("").trim()) / 100;
                            rtemp = Integer.parseInt(r.replaceAll("").trim()) / 100;

                            Log.v("heheheh",lhs.toString());
                            Log.v("heheheh",rhs.toString());

                            if (lhs.getCacheSize().indexOf("MB") != -1) {
                                ltemp *= 1024;
                            }

                            if (rhs.getCacheSize().indexOf("MB") != -1) {
                                rtemp *= 1024;
                            }
                        }

                        if (ltemp > rtemp) {
                            return -1;
                        } else if (ltemp < rtemp) {
                            return 1;
                        } else {
                            return 0;
                        }

                    }

                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public int getCount() {
            return cacheinfos.size();
        }

        public Object getItem(int position) {

            return cacheinfos.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            myView holder;
            CacheInfo info = cacheinfos.get(position);
            if (convertView == null) {
                view = View.inflate(CleanActivity.this,
                        R.layout.cache_clear_item, null);
                holder = new myView();
                holder.iv_icon = (ImageView) view
                        .findViewById(R.id.iv_cache_icon);
                holder.tv_name = (TextView) view.
                        findViewById(R.id.tv_cache_name);
                holder.tv_code = (TextView) view
                        .findViewById(R.id.tv_cache_code);
                holder.tv_data = (TextView) view
                        .findViewById(R.id.tv_cache_data);
                holder.tv_cache = (TextView) view
                        .findViewById(R.id.tv_cache_cache);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (myView) view.getTag();
            }
            holder.iv_icon.setImageDrawable(info.getIcon());
            holder.tv_name.setText(info.getName());
            holder.tv_code.setText("应用大小：" + info.getCodeSize());
            holder.tv_data.setText("数据大小：" + info.getDataSize());
            holder.tv_cache.setText("缓存大小：" + info.getCacheSize());
            return view;
        }

    }

    private class myView {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_cache;
        TextView tv_code;
        TextView tv_data;
    }

}
