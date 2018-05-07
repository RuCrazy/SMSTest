package ru.Card_SMS.solodov_sa.andrtest;

/**
 * Created by solodov_sa on 05.02.2018.
 */

public class msgData {
    String Body, Mask, Date;
    float Sum;

    msgData(String _body, String _mask, float _sum, String _date){
        Body = _body;
        Mask = _mask;
        Sum = _sum;
        Date = _date;
    }
}
