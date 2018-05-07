package ru.Card_SMS.solodov_sa.andrtest;

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
public class myItemsAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<MyItem> objects;

    myItemsAdapter (Context context, ArrayList<MyItem> items) {
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
        if (view == null) {
            view = lInflater.inflate(R.layout.myitems, parent, false);
        }

        MyItem p = getProduct(position);

        // заполняем View
        float _sum = 0;
        int _count = 0;
        ((TextView) view.findViewById(R.id.tvName)).setText(p.Name);
        for (int i = 0; i< p.MyElement.size(); i++ ) {
            _sum = _sum + p.MyElement.get(i).Sum;
            _count = _count + p.MyElement.get(i).SmsCount;
        }
        ((TextView) view.findViewById(R.id.tvSum)).setText(_sum + " р.");
        if (p.Name.equals(MainActivity.strReceipts)) {
            ((TextView) view.findViewById(R.id.tvSum)).setTextColor(MainActivity.ReceiptsTextColour);
        } else {
            ((TextView) view.findViewById(R.id.tvSum)).setTextColor(MainActivity.SpendTextColour);
        }
        ((TextView) view.findViewById(R.id.tvSmsCount)).setText(_count + "");
        return view;
    }

    // товар по позиции
    MyItem getProduct(int position) {
        return ((MyItem) getItem(position));
    }

}
