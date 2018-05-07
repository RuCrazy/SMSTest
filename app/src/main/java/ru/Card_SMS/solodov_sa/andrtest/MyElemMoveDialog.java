package ru.Card_SMS.solodov_sa.andrtest;

/**
 * Created by solodov_sa on 21.03.2016.
 */

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class MyElemMoveDialog extends DialogFragment implements OnClickListener {

    ArrayList<String> ItemsList = new ArrayList<String>();
    ListView lVItems;
    int Pos;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(MainActivity.MyItems.get(MainActivity.ItemPosition).MyElement.get(MainActivity.ElemPosition).Mask);
        View v = inflater.inflate(R.layout.elemmovedialog, null);
        v.findViewById(R.id.emOk).setOnClickListener(this);
        v.findViewById(R.id.emCancel).setOnClickListener(this);
        ListView lVItems = (ListView) v.findViewById(R.id.lVItemsNames);
        ArrayAdapter<String> ItemsListAdapter = new ArrayAdapter<String> (getActivity().getApplicationContext(), android.R.layout.simple_list_item_single_choice, ItemsList);
        lVItems.setAdapter(ItemsListAdapter);
        lVItems.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lVItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Pos = position;
                //MainActivity.MoveMyElem(MainActivity.ElemPosition, MainActivity.ItemPosition, position);
                //Toast.makeText(getActivity(),"ElemPos:" +  MainActivity.ElemPosition + " ItemPos:" + MainActivity.ItemPosition + " Pos:" + position, Toast.LENGTH_LONG).show();
                //MainActivity.UpdateMyItems();
                //MyItemActivity.ItemAdapter.notifyDataSetChanged();

            }
        });
        return v;
    }

    public void onClick(View v) {

        int id = ((Button) v).getId();
        switch (id) {
            case R.id.emOk:
                int position = 0;
                //Получаем номер группы в MyItems для перемещения в него элемента
                for (int i = 0; i<  MainActivity.MyItems.size(); i++){
                    if (ItemsList.get(Pos).equals(MainActivity.MyItems.get(i).Name)) {
                        position = i;
                    }
                }
                MainActivity.MoveMyElem(MainActivity.ElemPosition, MainActivity.ItemPosition, position);
                //Toast.makeText(getActivity(),"ElemPos:" +  MainActivity.ElemPosition + " ItemPos:" + MainActivity.ItemPosition + " Pos:" + Pos, Toast.LENGTH_LONG).show();
                MainActivity.UpdateMyItems();
                MyItemActivity.ItemAdapter.notifyDataSetChanged();
                break;
            case R.id.emCancel:
                //Toast.makeText(getActivity()," Отмена", Toast.LENGTH_LONG).show();
        }
        dismiss();
    }
    @Override
    public void onStart(){
        super.onStart();
        ItemsList.clear();
        for (int i = 0; i < MainActivity.MyItems.size(); i++){
            if (MainActivity.MyItems.get(i).Name != (MainActivity.MyItems.get(MainActivity.ItemPosition).Name)){
                ItemsList.add(MainActivity.MyItems.get(i).Name);
            }
        }


    }
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }
}