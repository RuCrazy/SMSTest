package ru.example.solodov_sa.andrtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

public class MyItemActivity extends Activity {

    TextView TV;
    ListView lvItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);

        lvItem = (ListView) findViewById(R.id.LvItem);

        TV = (TextView) findViewById(R.id.textView3);
        Intent intent = getIntent();
        int Position = intent.getIntExtra("Position", 0);
        TV.setText(Position + "  " + MainActivity.MyItems.get(Position).Name);
    }
}
