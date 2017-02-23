package org.sin.supervisor;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private static final int TO_THE_END = 0;

    private static final int LEAVE_FROM_END = 1;


    private int[] ids = {R.drawable.linkpage1,
            R.drawable.linkpage2, R.drawable.linkpage3,
            R.drawable.linkpage4};

    private List<View> guides = new ArrayList<View>();
    private ViewPager pager;
    private ImageView start;
    private ImageView curDot;
    private LinearLayout dotContain;
    private int offset;
    private int curPos = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        sp = getSharedPreferences("ListenConfig", MODE_PRIVATE);

        editor = sp.edit();
        editor.putString("guide_activity", "true");
        editor.commit();
        init();
    }

    private ImageView buildImageView(int id) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(id);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);
        iv.setLayoutParams(params);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        return iv;
    }


    private void init() {
        this.getView();
        initDot();
        ImageView iv = null;
        guides.clear();
        for (int i = 0; i < ids.length; i++) {
            iv = buildImageView(ids[i]);
            guides.add(iv);
        }

        System.out.println("guild_size=" + guides.size());


        curDot.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {

                        offset = curDot.getWidth();
                        return true;
                    }
                });

        final GuidePagerAdapter adapter = new GuidePagerAdapter(guides);

        pager.setAdapter(adapter);        pager.clearAnimation();

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                int pos = position % ids.length;

                moveCursorTo(pos);

                if (pos == ids.length - 1) {
                    handler.sendEmptyMessageDelayed(TO_THE_END, 500);

                } else if (curPos == ids.length - 1) {
                    handler.sendEmptyMessageDelayed(LEAVE_FROM_END, 100);
                }
                curPos = pos;
                super.onPageSelected(position);
            }
        });

    }


    private void getView() {
        dotContain = (LinearLayout) this.findViewById(R.id.dot_contain);
        pager = (ViewPager) findViewById(R.id.contentPager);
        curDot = (ImageView) findViewById(R.id.cur_dot);
        start = (ImageView) findViewById(R.id.open);
        start.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                GuideActivity.this.finish();
            }
        });
    }


    private boolean initDot() {

        if (ids.length > 0) {
            ImageView dotView;
            for (int i = 0; i < ids.length; i++) {
                dotView = new ImageView(this);
                dotView.setImageResource(R.drawable.dot1_w);
                dotView.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

                dotContain.addView(dotView);
            }
            return true;
        } else {
            return false;
        }
    }


    private void moveCursorTo(int position) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation tAnim =
                new TranslateAnimation(offset * curPos, offset * position, 0, 0);
        animationSet.addAnimation(tAnim);
        animationSet.setDuration(300);
        animationSet.setFillAfter(true);
        curDot.startAnimation(animationSet);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TO_THE_END)
                start.setVisibility(View.VISIBLE);
            else if (msg.what == LEAVE_FROM_END)
                start.setVisibility(View.GONE);
        }
    };


    class GuidePagerAdapter extends PagerAdapter {

        private List<View> views;

        public GuidePagerAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1 % views.size()));
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {

            return views.size() * 20;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            Log.e("tag", "instantiateItem = " + arg1);
            ((ViewPager) arg0).addView(views.get(arg1 % views.size()), 0);
            return views.get(arg1 % views.size());
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }


    }
}
