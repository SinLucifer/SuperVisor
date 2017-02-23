package org.sin.supervisor.blackNumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.sin.supervisor.MainActivity;
import org.sin.supervisor.R;

import java.util.List;


/**
 * Created by Sin on 2015/9/27.
 */
public class BlackNumberSet extends Activity {
    private BlackNumberDao dao;
    private BlackNumberadapter adapter;
    private Intent intent;

    private EditText edit;
    private ListView black_number_list;
    private ImageView add_black_number;
    private ImageView back;

    private String number;
    private String oldnumber;
    private String newNumber;
    private List<String> numbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blacknumber_view);

        dao = new BlackNumberDao(this);

        black_number_list = (ListView) findViewById(R.id.black_number_list);
        registerForContextMenu(black_number_list);

        add_black_number = (ImageView) findViewById(R.id.add_black_number);
        add_black_number.setOnClickListener(new AddOnclick());

        back = (ImageView) findViewById(R.id.back2);
        back.setOnClickListener(new BackOnclick());

        numbers = dao.getAllNumbers();
        adapter = new BlackNumberadapter();

        black_number_list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        intent = getIntent();
        number = intent.getStringExtra("number");
       // Log.d("Sin","show number"+number);
        if (number != null) {
            showInputDialog(number);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.blackmenu, menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int id = (int) info.id;
        String number = numbers.get(id);
        switch (item.getItemId()) {
            case R.id.update_number:
                updateNumber(number);
                break;
            case R.id.delete_number:
                dao.delete(number);
                numbers = dao.getAllNumbers();
                adapter.notifyDataSetChanged();
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void updateNumber(final String number) {
        AlertDialog.Builder update_builder = new AlertDialog.Builder(BlackNumberSet.this);
        update_builder.setTitle("更改黑名单电话号码");
        edit = new EditText(this);
        edit.setInputType(InputType.TYPE_CLASS_PHONE);

        update_builder.setView(edit);

        oldnumber = number;

        //Log.d("Sin~", "OK!" + oldnumber + "~");
        update_builder.setPositiveButton("更改", new PositChangeOnclick());
        update_builder.setNegativeButton("返回", new NegatOnclick());

        update_builder.create().show();
    }

    private void showInputDialog(String number) {
        AlertDialog.Builder b_numberbuilder = new AlertDialog.Builder(BlackNumberSet.this);
        b_numberbuilder.setTitle("添加黑名单");
        edit = new EditText(BlackNumberSet.this);
        edit.setInputType(InputType.TYPE_CLASS_PHONE);

        b_numberbuilder.setView(edit);

        if (number!=null){
            edit.setText(number);
        }

        b_numberbuilder.setPositiveButton("添加", new PositOnclick());
        b_numberbuilder.setNegativeButton("返回", new NegatOnclick());

        b_numberbuilder.create().show();
    }

    private class AddOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            showInputDialog("");
        }
    }

    private class BackOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            intent = new Intent(BlackNumberSet.this, MainActivity.class);
            startActivity(intent);
            BlackNumberSet.this.finish();
        }
    }

    private class PositChangeOnclick implements Dialog.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {

            newNumber = edit.getText().toString().trim();

            if (newNumber.length() != 11) {
                Toast.makeText(BlackNumberSet.this, "您输入的手机号有误，请查询后重新输入！"
                        , Toast.LENGTH_LONG).show();
            } else {
              //  Log.d("Sin~", "OK!"+newNumber+"~"+oldnumber);
                dao.update(newNumber, oldnumber);
                numbers = dao.getAllNumbers();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class PositOnclick implements Dialog.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            number = edit.getText().toString().trim();
            if (number.length() != 11) {
                Toast.makeText(BlackNumberSet.this, "您输入的手机号有误，请查询后重新输入！"
                        , Toast.LENGTH_LONG).show();
                return;
            } else {
                dao.add(number);
                numbers = dao.getAllNumbers();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class NegatOnclick implements Dialog.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    private class BlackNumberadapter extends BaseAdapter {
        public int getCount() {
            // TODO Auto-generated method stub
            return numbers.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return numbers.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(BlackNumberSet.this, R.layout.blacknumber_item, null);
            TextView tv = (TextView) view.findViewById(R.id.tv_blacknumber_item);
            tv.setText(numbers.get(position));
            return view;
        }
    }
}
