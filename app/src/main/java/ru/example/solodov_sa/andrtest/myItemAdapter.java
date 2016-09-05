package ru.example.solodov_sa.andrtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by solodov_sa on 04.08.2015.
 */
public class myItemAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<MyElem> objects;

    myItemAdapter(Context context, ArrayList<MyElem> items) {
        ctx = context;
        objects = items;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        //if (view == null) {
            view = lInflater.inflate(R.layout.myitems, parent, false);
        //}
        MyElem p = getProduct(position);

        if ((p.SmsCount == 0) & (MainActivity.SettingsHideNullItem)){
            view = lInflater.inflate(R.layout.null_item,parent, false);
           // view.setVisibility(View.GONE);
        }else{
            // заполняем View
            ((TextView) view.findViewById(R.id.tvName)).setText(p.Mask);
            ((TextView) view.findViewById(R.id.tvSum)).setText(p.Sum + " р.");
            ((TextView) view.findViewById(R.id.tvSmsCount)).setText(p.SmsCount + "");
        }

        return view;
    }

    // товар по позиции
    MyElem getProduct(int position) {
        return ((MyElem) getItem(position));
    }

}
