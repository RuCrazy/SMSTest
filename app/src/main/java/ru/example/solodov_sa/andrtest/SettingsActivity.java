package ru.example.solodov_sa.andrtest;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    CheckBox CBNullItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Настройки");
        getActionBar().setIcon(R.drawable.setico);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        Toast.makeText(this,id + " ", Toast.LENGTH_LONG).show();
        switch (id) {
            case R.id.home:
                Toast.makeText(this,"Home", Toast.LENGTH_LONG).show();
                finish();
            break;
            case R.id.homeAsUp:
                Toast.makeText(this,"HomeAsUp", Toast.LENGTH_LONG).show();
                finish();
                break;

        }
        return true;
    }

}
