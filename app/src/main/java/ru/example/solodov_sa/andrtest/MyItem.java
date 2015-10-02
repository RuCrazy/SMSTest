package ru.example.solodov_sa.andrtest;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by solodov_sa on 04.08.2015.
 */
public class MyItem {

    String Name;
    ArrayList<String> Mask;
    float Sum;
    int SmsCount;

    MyItem(String _name, ArrayList<String> _mask, float _sum, int _count) {
        Name = _name;
        Mask = _mask;
        Sum = _sum;
        SmsCount = _count;
    }

}
