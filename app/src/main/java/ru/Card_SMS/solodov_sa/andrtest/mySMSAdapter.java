package ru.Card_SMS.solodov_sa.andrtest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class mySMSAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<msgData> objects;

    mySMSAdapter(Context context, ArrayList<msgData> items) {
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
        if (view == null)
        {
            view = lInflater.inflate(R.layout.sms_list, parent, false);
        }
        msgData p = getProduct(position);
            // заполняем View
        ((TextView) view.findViewById(R.id.tvDate)).setText(p.Date);
        ((TextView) view.findViewById(R.id.tvSMSSum)).setText(p.Sum + " р.");
        ((TextView) view.findViewById(R.id.tvBody)).setText(p.Body);

        return view;
    }

    // CМС по позиции
    msgData getProduct(int position) {
        return ((msgData) getItem(position));
    }

}
