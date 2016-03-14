package ru.example.solodov_sa.andrtest;

import java.util.ArrayList;

/**
 * Created by solodov_sa on 04.08.2015.
 */
public class MyElem {

    ArrayList<String> Mask;
    ArrayList<Float> Sum;
    ArrayList<Integer> SmsCount;

    MyElem( ArrayList<String> _mask, ArrayList<Float> _sum, ArrayList<Integer> _count) {
        Mask = _mask;
        Sum = _sum;
        SmsCount = _count;
    }

}
