package ru.Card_SMS.solodov_sa.andrtest;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends Activity {

    CheckBox chbNullItems,chbCurenncy;
    Spinner spCurenncy;
    EditText etCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Настройки");
        getActionBar().setIcon(R.drawable.setico);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);

        chbNullItems = (CheckBox) findViewById(R.id.chbHideZeroElem);
        chbNullItems.setChecked(MainActivity.SettingsHideNullItem);
        chbNullItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.SettingsHideNullItem = chbNullItems.isChecked();
            }
        });

        chbCurenncy = (CheckBox) findViewById(R.id.chbShowCurenncy);
        chbCurenncy.setChecked(MainActivity.settingsShowCurenncy);
        chbCurenncy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.settingsShowCurenncy = chbCurenncy.isChecked();
                if (chbCurenncy.isChecked()) {
                    spCurenncy.setEnabled(true);
                    spCurenncy.setSelection(MainActivity.curenncyPosition);
                } else {
                    spCurenncy.setEnabled(false);
                }
                MainActivity.SetCurenncy();
            }
        });

        spCurenncy = (Spinner) findViewById(R.id.spCurenncy);
        ArrayAdapter<String> spinerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item , MainActivity.arrayCurenncyData);
        spinerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCurenncy.setAdapter(spinerAdapter);
        spCurenncy.setSelection(MainActivity.curenncyPosition);
        spCurenncy.setEnabled(chbCurenncy.isChecked());
        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                MainActivity.curenncyPosition = position;
                MainActivity.curenncy = MainActivity.arrayCurenncy[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
         spCurenncy.setOnItemSelectedListener(itemSelectedListener);

         etCard = (EditText) findViewById(R.id.etCard);
         etCard.setText(MainActivity.Card);
         etCard.addTextChangedListener( new TextWatcher(){
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void afterTextChanged(Editable s) {
                 MainActivity.Card = s.toString();

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {
                 MainActivity.Card = etCard.getText().toString();
             }
         });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

}
