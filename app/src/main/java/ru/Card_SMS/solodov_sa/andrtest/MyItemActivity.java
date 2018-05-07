package ru.Card_SMS.solodov_sa.andrtest;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyItemActivity extends Activity {

    TextView TVName;
    ListView lvItem;
    public static String SMSMask;
    static myItemAdapter ItemAdapter;
    //ArrayList<MyElem> MyElements = new ArrayList<MyElem>();
    DialogFragment ElemMaskDialog, ElemMoveDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);
        setTitle(MainActivity.MyItems.get(MainActivity.ItemPosition).Name);
        getActionBar().setIcon(R.drawable.detail);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ElemMaskDialog = new MyElemMaskDialog();
        ElemMoveDialog = new MyElemMoveDialog();

        //TVName = (TextView) findViewById(R.id.textView3);
        //TVName.setText(MainActivity.MyItems.get(MainActivity.ItemPosition).Name);

        ItemAdapter = new myItemAdapter(this, MainActivity.MyItems.get(MainActivity.ItemPosition).MyElement);
        lvItem = (ListView) findViewById(R.id.LvItem);
        lvItem.setAdapter(ItemAdapter);
        lvItem.setDivider(getResources().getDrawable(android.R.color.transparent));

        //Button BAdd = (Button) findViewById(R.id.BAddItem);

        //getElements(Position);
        ItemAdapter.notifyDataSetChanged();

        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent( MyItemActivity.this, mySmsActivity.class);
                SMSMask = MainActivity.MyItems.get(MainActivity.ItemPosition).MyElement.get(position).Mask;
                startActivity(intent);
            }
        });

        View.OnClickListener oclBAdd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ElemMaskDialog.show(getFragmentManager(), "");
            }
        };
        //BAdd.setOnClickListener(oclBAdd);
        registerForContextMenu(lvItem);
    }
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo){
        //LastCMVID = v.getId();
        switch (v.getId()) {
            case R.id.LvItem:
                getMenuInflater().inflate(R.menu.menu_lvelems, menu);
                break;
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //if (LastCMVID == R.id.lvItems){
            switch (item.getItemId()){
                case  R.id.delelem: //Удаляем элемент
                    MainActivity.MyItems.get(MainActivity.ItemPosition).MyElement.remove(info.position);
                    MainActivity.ItemsAdapter.notifyDataSetChanged();
                    ItemAdapter.notifyDataSetChanged();
                    return true;
                case R.id.moveelem: //Перемещаем элемент
                    MainActivity.ElemPosition = info.position;
                    ElemMoveDialog.show(getFragmentManager(), "");
                    return true;
                case R.id.moveelem_other: //Перемещаем элемент в прочее
                    if (MainActivity.ItemPosition < MainActivity.MyItems.size() - 1) {
                        MainActivity.MoveMyElem(info.position, MainActivity.ItemPosition, MainActivity.MyItems.size() - 1);
                        MainActivity.ItemsAdapter.notifyDataSetChanged();
                        ItemAdapter.notifyDataSetChanged();
                    }
                    return true;
            }
        return super.onContextItemSelected(item);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //public void getElements(int Position){
    //    for (int i = 0; i < MainActivity.MyItems.get(Position).MyElement.size(); i++){
    //        MyElements.add(MainActivity.MyItems.get(Position).MyElement.get(i));
    //    }

    //}
}

