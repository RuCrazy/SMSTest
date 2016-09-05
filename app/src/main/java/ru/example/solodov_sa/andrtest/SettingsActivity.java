package ru.example.solodov_sa.andrtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class SettingsActivity extends Activity {

    CheckBox CBNullItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        CBNullItems = (CheckBox) findViewById(R.id.checkBox);
        CBNullItems.setChecked(MainActivity.SettingsHideNullItem);
        CBNullItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SettingsHideNullItem = CBNullItems.isChecked();
            }
        });
    }
}
