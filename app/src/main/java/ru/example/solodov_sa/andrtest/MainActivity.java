package ru.example.solodov_sa.andrtest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends Activity implements DatePickerFragment.TheListener {

    Button myBtn;
    TextView MyTV, MyTV2;
    Button myBtn2, myBtn3;
    EditText MyTxt;
    float Sum;

    ArrayAdapter<String> smsadapter;
    String Sender, ReqDate, TxtMask, FilePath;

    TextView lblMsg, lblNo;
    ListView lvMsg,lvItems;

    private int cur_year, cur_month, cur_day;

    ArrayList<String> msgData = new ArrayList<String>();

    ArrayList<MyItem> MyItems = new ArrayList<MyItem>();
    myItemsAdapter ItemsAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ItemsAdapter = new myItemsAdapter(this, MyItems);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(ItemsAdapter);
        lvItems.setDivider(getResources().getDrawable(android.R.color.transparent));

        msgData.clear();
        smsadapter = new ArrayAdapter<String>(this, R.layout.sms_list, msgData);
        lvMsg = (ListView) findViewById(R.id.lvMsg);
        lvMsg.setAdapter(smsadapter);

        MyTV = (TextView) findViewById(R.id.textView);
        MyTV2 = (TextView) findViewById(R.id.textView2);
        Button myBtn = (Button) findViewById(R.id.button);
        Button myBtn2 = (Button) findViewById(R.id.button2);
        Button myBtn3 = (Button) findViewById(R.id.button3);
        final EditText MyTxt = (EditText) findViewById(R.id.editText);
        MyTxt.setMaxLines(1);

        Sender = "900";
        ReqDate = "06.2015";
        cur_year = 2015;
        cur_month = 6;
        cur_day = 1;
        Sum = 0;

        FilePath = getExternalFilesDir(null).toString()+ "/Settings.txt" ;


        MyTV2.setText(ReqDate);

        //Получить список СМС
        GetSMS();
        ReadData();

        // MyTV.setText(FilePath);

        //Нажатие на кнопку "Отбор"
        View.OnClickListener oclBtn1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FindSMS(MyTxt.getText().toString());
            }
        };
        myBtn.setOnClickListener(oclBtn1);


        View.OnClickListener oclBtn2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                GetSMS();
            }
        };
        myBtn2.setOnClickListener(oclBtn2);


        //Нажатие на кнопку "Дата"
        View.OnClickListener oclBtn3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putInt(DatePickerFragment.YEAR, cur_year);
                b.putInt(DatePickerFragment.MONTH, cur_month - 1);
                b.putInt(DatePickerFragment.DATE, cur_day);
                DialogFragment DP = new DatePickerFragment();
                DP.setArguments(b);
                DP.show(getFragmentManager(), "DP");


            }

        };
        myBtn3.setOnClickListener(oclBtn3);

    }
    private void FindSMS(String Text){
        // TODO Auto-generated method stub
        String str, str2;
        int j = 0;
        str2 = "";
        int k;
        int l;
        Sum = 0;
        TxtMask = Text;
        TxtMask = TxtMask.toLowerCase();

        if (!TxtMask.equals("")) {
            for (int i = 0; i < msgData.size(); i++) {
                str = msgData.get(i);
                str = str.toLowerCase();

                if (str.indexOf(TxtMask) > 0) {
                    j++;
                    k = str.indexOf("покупка");
                    if (k > -1) {
                        k = k + 8;
                        l = str.indexOf("р");
                        Sum = Sum + Float.parseFloat(str.substring(k, l));
                        str2 = str.substring(k, l);

                    }


                }
            }
            //lvMsg.setAdapter(cursor);
            MyTV.setText("Найдено " + j + " SMS с ключом: " + TxtMask + "\n" + Sum + " р.");
            ArrayList<String> MA = new ArrayList<String>();
            MA.add(TxtMask);
            MyItems.add(new MyItem(TxtMask, MA, Sum, j));
        }
    }

    //чтение файла с настройками
    private void ReadData() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show();
            return;
        } else {
            File fhandle = new File(FilePath);
            try {
                FileInputStream inStream = new FileInputStream(fhandle);

                if ( inStream != null ) {
                    MyItems.clear();
                    InputStreamReader inputStreamReader = new InputStreamReader(inStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    //StringBuilder stringBuilder = new StringBuilder();

                    while ( (receiveString = bufferedReader.readLine()) != null ) {
                        //stringBuilder.append(receiveString);
                        int num = Integer.parseInt(receiveString.toString());
                        if (num != 0){
                            for (int i = 0; i <= num-1; i++) {
                                String name = bufferedReader.readLine();
                                int SmsCount = Integer.parseInt(bufferedReader.readLine().toString());
                                Float sum = Float.parseFloat(bufferedReader.readLine().toString());
                                ArrayList<String> MA = new ArrayList<String>();
                                int num2 = Integer.parseInt(bufferedReader.readLine().toString());
                                for (int j = 0; j <= num2-1; j++) {
                                    MA.add(bufferedReader.readLine().toString());
                                    FindSMS(MA.get(j));
                                }
                               // MyItems.add(new MyItem(name, MA, sum, SmsCount));
                            }

                        }

                    }

                    inStream.close();
                    //ret = stringBuilder.toString();
                }

            }
            catch (FileNotFoundException e) {
                //Log.e("login activity", "File not found: " + e.toString());
                Toast.makeText(this, "Файл с настройками не найден: " + e.toString(), Toast.LENGTH_LONG).show();
                MyTV.setText("Файл с настройками не найден: " + e.toString());
            } catch (IOException e) {
                //Log.e("login activity", "Can not read file: " + e.toString());
                Toast.makeText(this, "Ошибка чтения файла: " + e.toString(), Toast.LENGTH_LONG).show();
            }
       }
    }
    //Запись файла с настройками
    private void SaveData() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show();
            return;
        } else {

            File fhandle = new File(FilePath);
            try
            {
                //Если нет директорий в пути, то они будут созданы:
                if (!fhandle.getParentFile().exists()) {
                    fhandle.getParentFile().mkdirs();
                }
                //Если файл существует, то он будет перезаписан:
                fhandle.createNewFile();
                FileOutputStream fOut = new FileOutputStream(fhandle);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

                String LS = "\r\n";
                myOutWriter.write(String.valueOf(MyItems.size()));
                //Перебираем все элементы MyItems и записываем значения в файл
                for (int i = 0; i < MyItems.size(); i++){
                    myOutWriter.write(LS + MyItems.get(i).Name);
                    myOutWriter.write(LS + MyItems.get(i).SmsCount);
                    myOutWriter.write(LS + String.valueOf(MyItems.get(i).Sum));
                    myOutWriter.write(LS + String.valueOf(MyItems.get(i).Mask.size()));
                    for (int j = 0; j < MyItems.get(i).Mask.size(); j++){
                        myOutWriter.write(LS + MyItems.get(i).Mask.get(j));
                    }
                }
                myOutWriter.close();
                fOut.close();

                Toast.makeText(this, "Файл записан на SD-карту" + FilePath, Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                Toast.makeText(this,"Path " + FilePath + ", " + e.toString(), Toast.LENGTH_LONG).show();
            }


        }



    }

    private void GetSMS() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

        if (cursor.moveToFirst()) { // must check the result to prevent exception

            int i = 0;
            String SmsDate = "";
            // String TxtBody = "";
            smsadapter.clear();
            do {
                SmsDate = new SimpleDateFormat("MM.yyyy").format(cursor.getLong(5));
                if (Sender.equals(cursor.getString(2)) && SmsDate.equals(ReqDate))   {

                    smsadapter.add("Дата:" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cursor.getLong(5)) + "; От:" + cursor.getString(2) + "; Текст:" + cursor.getString(13));
                    //MyItems.add(new MyItem("Test11111", i, i+i ));
                    i++;
                }
                //for(int idx=0;idx<cursor.getColumnCount();idx++)
                //{
                //    msgData = new String[] {cursor.getColumnName(idx) + ":" + cursor.getString(idx)};
                //}
                // use msgData
                //msgData = new String[] {"Дата:" + cursor.getString(5) + "; От:" + cursor.getString(2) + "; Текст:" + cursor.getString(13)};

                //MyTV.setText(msgData);

            } while (cursor.moveToNext());
            MyTV.setText("Получено " + i + " SMS");

        } else {
            // empty box, no SMS
            MyTV.setText("Список SMS пуст");
            smsadapter.clear();
            //lvMsg.setAdapter(adapter);

        }
        Cursor close;


    }

    @Override
    public void onDestroy(){

        SaveData();
        super.onDestroy();

    }


    @Override
    public void returnDate(int year, int month, String date) {
        // TODO Auto-generated method stub
        cur_month = month + 1;
        cur_year = year;
        ReqDate = date;
        MyTV2.setText(ReqDate);
        GetSMS();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.file_save:
                SaveData();
                return true;
            case R.id.file_read:
                ReadData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
