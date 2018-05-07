package ru.Card_SMS.solodov_sa.andrtest;

import android.os.Bundle;
import android.app.Activity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class mySmsActivity extends Activity {

    ListView lvSMS;
    static mySMSAdapter SMSAdapter;
    ArrayList <msgData> SMSList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sms);

        setTitle(MyItemActivity.SMSMask);
        getActionBar().setIcon(R.drawable.sms);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        SMSList.clear();
        for (int i=0; i < MainActivity.MsgData.size() ;i++)
            if (MainActivity.MsgData.get(i).Mask.equals(MyItemActivity.SMSMask))
            {
                SMSList.add(MainActivity.MsgData.get(i));
            }

        SMSAdapter = new mySMSAdapter(this, SMSList);
        lvSMS = (ListView) findViewById(R.id.LvSMS);
        lvSMS.setAdapter(SMSAdapter);
        lvSMS.setDivider(getResources().getDrawable(android.R.color.transparent));
        SMSAdapter.notifyDataSetChanged();
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
}
