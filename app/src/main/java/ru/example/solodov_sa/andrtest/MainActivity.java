package ru.example.solodov_sa.andrtest;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
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
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends Activity implements DatePickerFragment.TheListener {

    Button myBtn;
    TextView MyTV, MyTV2, MyTV3, TVBalance1, TVBalance2, TVBalance3;
    Button myBtn2, myBtn3;
    float Sum, Total;

    ArrayAdapter<String> smsadapter;
    String Sender, ReqDate, TxtMask, FilePath;

    int LastCMVID;

    TextView lblMsg, lblNo;
    ListView lvMsg,lvItems;

    private int cur_year, cur_month, cur_day;

    static ArrayList<String> msgData = new ArrayList<String>();

    static ArrayList<MyItem> MyItems = new ArrayList<MyItem>();
    static ArrayList<MyItem> MyItemsHideNull = new ArrayList<MyItem>();
    static myItemsAdapter ItemsAdapter;

    DialogFragment ItemsNameDialog;

    static int ItemPosition, ElemPosition;
    static  boolean NewItem, SettingsHideNullItem;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SettingsHideNullItem = true;

        ItemsNameDialog = new MyItemsNameDialog();

        ItemsAdapter = new myItemsAdapter(this, MyItems);
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(ItemsAdapter);
        lvItems.setDivider(getResources().getDrawable(android.R.color.transparent));

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, MyItemActivity.class);
                ItemPosition = position;
                startActivity(intent);
            }
        });

        Total  = 0;

        registerForContextMenu(lvItems);

        msgData.clear();
        smsadapter = new ArrayAdapter<String>(this, R.layout.sms_list, msgData);
        lvMsg = (ListView) findViewById(R.id.lvMsg);
        lvMsg.setAdapter(smsadapter);

        TVBalance1 = (TextView) findViewById(R.id.textView);
        TVBalance2 = (TextView) findViewById(R.id.textView5);
        TVBalance3 = (TextView) findViewById(R.id.textView6);
        MyTV2 = (TextView) findViewById(R.id.textView2);
        Button myBtn = (Button) findViewById(R.id.button);
        Button myBtn2 = (Button) findViewById(R.id.button2);
        Button myBtn3 = (Button) findViewById(R.id.button3);

        //Номер отправителя SMS
        Sender = "900";
        //Получаем текущую дату
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM.yyyy");
        ReqDate = df.format(c.getTime());
        df = new SimpleDateFormat("yyyy");
        cur_year = Integer.parseInt(df.format(c.getTime()));
        df = new SimpleDateFormat("MM");
        cur_month = Integer.parseInt(df.format(c.getTime()));;
        cur_day = 1;
        Sum = 0;


        FilePath = getExternalFilesDir(null).toString()+ "/Settings.txt" ;


        df = new SimpleDateFormat("MMM.yyyy");
        MyTV2.setText(df.format(c.getTime()));

        //Получить список СМС
        GetSMS();
        //Читаем файл настроек
        ReadData();
        ItemOther();
        GetElements();
        UpdateMyItems();
        CalculateTotal();
        // MyTV.setText(FilePath);


        //Нажатие на кнопку "Добавить"
        View.OnClickListener oclBtn1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewItem = true;
                ItemPosition = MyItems.size()-1;
                AddMyItem(ItemPosition,"NewGroup");

                ItemsNameDialog.show(getFragmentManager(), "");
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
    //Проверка на наличие и создание группы "Прочее" и "Поступления"
    public void ItemOther(){
        boolean f,f2;
        f = false;
        f2 = false;
        for (int i = 0; i < MyItems.size(); i++){
            if (MyItems.get(i).Name.equals("Прочее")) {
                f = true;
            }
            if (MyItems.get(i).Name.equals("Поступления")) {
                f2 = true;
            }
        }
        if (!f) {
            AddMyItem(MyItems.size(),"Прочее");
        }
        if (!f2) {
            AddMyItem(0,"Поступления");
        }

    }

    //Поиск всех объектов element из SMS
    public void GetElements(){
        String str, str2;
        boolean f;
        for (int i = 0; i < msgData.size(); i++) {
            str = msgData.get(i);
            str.toLowerCase();
            str2 = str;
            if (str.indexOf("покупка") > 0) {
                str = str.substring(str.indexOf("покупка") + 8);
                //MyTV.setText(str);
                str = str.substring(str.indexOf("р")+2);
                //MyTV.setText(MyTV.getText() + " -- " + str);
                str = str.substring(0,str.indexOf("Баланс")-1);
                //MyTV.setText(MyTV.getText() + " -- " + str);
                //MyTV.setText(str);
                f = false;
                for (int n = 0; n < MyItems.size(); n++) {
                    for (int j = 0; j < MyItems.get(n).MyElement.size(); j++) {
                        if (str.equals(MyItems.get(n).MyElement.get(j).Mask)) {
                            f = true;
                            //MyTV.setText(str + " " + MyItems.size());
                        }
                    }
                }
                if (!f) {
                    AddMyElem(str, MyItems.size()-1);
                }
            }
            if (str.indexOf("выдача наличных") > 0) {
                str = str.substring(str.indexOf("выдача наличных"),str.indexOf("выдача наличных") + 15);
                f = false;
                for (int n = 0; n < MyItems.size(); n++) {
                    for (int j = 0; j < MyItems.get(n).MyElement.size(); j++) {
                        if (str.equals(MyItems.get(n).MyElement.get(j).Mask)) {
                            f = true;
                        }
                    }
                }
                if (!f) {
                    AddMyElem(str, MyItems.size()-1);
                }
            }
            if (str.indexOf("оплата услуг") > 0) {
                str = str.substring(str.indexOf("оплата услуг"),str.indexOf("оплата услуг") + 12);
                f = false;
                for (int n = 0; n < MyItems.size(); n++) {
                    for (int j = 0; j < MyItems.get(n).MyElement.size(); j++) {
                        if (str.equals(MyItems.get(n).MyElement.get(j).Mask)) {
                            f = true;
                        }
                    }
                }
                if (!f) {
                    AddMyElem(str, MyItems.size()-1);
                }
            }
            if (str2.indexOf("зачисление") > 0) {
                //str2 = str2.substring(str2.indexOf("зачисление") + 11);
                //MyTV.setText(str2);
                str2 = str2.substring(str2.indexOf("р")+2);
                //MyTV.setText(MyTV.getText() + " -- " + str2);
                //str2 = str2.substring(0,10);
                str2 = str2.substring(0,str2.indexOf("Баланс"));
                //MyTV.setText(MyTV.getText() + " -- " + str2);
                f = false;
                for (int n = 0; n < MyItems.size(); n++) {
                    for (int j = 0; j < MyItems.get(n).MyElement.size(); j++) {
                        if (str2.equals(MyItems.get(n).MyElement.get(j).Mask)) {
                            f = true;
                            //MyTV.setText(str2 + " " + MyItems.size());
                        }
                    }
                }
                if (!f) {
                    AddMyElem(str2, 0);
                }
            }
        }
    }

    //Обновление информации MyItems из SMS
    public static void UpdateMyItems(){
        String str, TxtMask;
        int k, l, num;
        float Sum;

        for (int i = 0; i < MyItems.size(); i++) {
            Sum = 0;
            num = 0;
            for (int n = 0; n < MyItems.get(i).MyElement.size(); n++) {
                TxtMask = MyItems.get(i).MyElement.get(n).Mask;
                TxtMask = TxtMask.toLowerCase();
                for (int j = 0; j < msgData.size(); j++) {
                    str = msgData.get(j);
                    str = str.toLowerCase();

                    if (str.indexOf(TxtMask) > 0) {
                        num ++;
                        k = str.indexOf("покупка");
                        if (k > -1) {
                            k = k + 8;
                            l = str.indexOf("р");
                            Sum = Sum + Float.parseFloat(str.substring(k, l));
                        }
                        k = str.indexOf("выдача наличных");
                        if (k > -1) {
                            k = k + 15;
                            l = str.indexOf("р");
                            Sum = Sum + Float.parseFloat(str.substring(k, l));
                        }
                        k = str.indexOf("оплата услуг");
                        if (k > -1) {
                            k = k + 12;
                            l = str.indexOf("р");
                            Sum = Sum + Float.parseFloat(str.substring(k, l));
                        }
                        k = str.indexOf("зачисление");
                        if (k > -1) {
                            k = k + 11;
                            l = str.indexOf("р");
                            Sum = Sum + Float.parseFloat(str.substring(k, l));
                            //str2 = str.substring(k, l);
                        }

                    }
                }
                BigDecimal d = new BigDecimal(String.valueOf(Sum));
                d = d.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                Sum = d.floatValue();
                MyItems.get(i).MyElement.get(n).SmsCount = num;
                MyItems.get(i).MyElement.get(n).Sum = Sum;
                num = 0;
                Sum = 0;
            }
        }
        ItemsAdapter.notifyDataSetChanged();
    }
    //Добавление элемента MyItem
    private void AddMyItem(int Pos, String Name){

        ArrayList<MyElem> _MyElem = new ArrayList<MyElem>();
        _MyElem.clear();
        MyItem _MyItem = new MyItem(Name, _MyElem);
        MyItems.add(Pos, _MyItem);
    }
    //Добавление элемента MyElem
    public static void AddMyElem(String Mask, int i){
        float j = 0;
        MyElem _Elem = new MyElem(Mask, j, 0);
        MyItems.get(i).MyElement.add(_Elem);
    }
    //Перемещение элемента MyElem
    public static void MoveMyElem(int _Elem, int _OldItem, int _NewItem){
        MyItems.get(_NewItem).MyElement.add(MyItems.get(_OldItem).MyElement.get(_Elem));
        MyItems.get(_OldItem).MyElement.remove(_Elem);
    }
    //Добавление элемента MyItems
    private void AddMyItems(String Text){
        String str;
        int j = 0;
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
                    }
                }
            }
            //lvMsg.setAdapter(cursor);
            //MyTV.setText("Найдено " + j + " SMS с ключом: " + TxtMask + "\n" + Sum + " р.");
            MyElem _Elem = new MyElem(TxtMask, Sum, j);
            ArrayList<MyElem> _MyElem = new ArrayList<MyElem>();
            _MyElem.add(_Elem);
            MyItem _MyItem = new MyItem(TxtMask, _MyElem);
            MyItems.add(_MyItem);
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
                                AddMyItem(MyItems.size(),name);
                               // int SmsCount = Integer.parseInt(bufferedReader.readLine().toString());
                               // Float sum = Float.parseFloat(bufferedReader.readLine().toString());
                                //ArrayList<String> MA = new ArrayList<String>();
                                int num2 = Integer.parseInt(bufferedReader.readLine().toString());
                                for (int j = 0; j <= num2-1; j++) {
                                    String Mask = bufferedReader.readLine().toString();
                                    //AddMyItems(MA.get(j));
                                    AddMyElem(Mask, i);
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
                //MyTV.setText("Файл с настройками не найден: " + e.toString());
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
                if (SettingsHideNullItem){
                    myOutWriter.write("1");
                }else {myOutWriter.write("0");}
                myOutWriter.write(String.valueOf(MyItems.size()));
                //Перебираем все элементы MyItems и записываем значения в файл
                for (int i = 0; i < MyItems.size(); i++){
                    myOutWriter.write(LS + MyItems.get(i).Name);
                   // myOutWriter.write(LS + MyItems.get(i).SmsCount);
                   // myOutWriter.write(LS + String.valueOf(MyItems.get(i).Sum));
                    myOutWriter.write(LS + String.valueOf(MyItems.get(i).MyElement.size()));
                    for (int j = 0; j < MyItems.get(i).MyElement.size(); j++){
                        myOutWriter.write(LS + MyItems.get(i).MyElement.get(j).Mask);
                    }
                }
                myOutWriter.close();
                fOut.close();

                //Toast.makeText(this, "Файл записан на SD-карту" + FilePath, Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                //Toast.makeText(this,"Path " + FilePath + ", " + e.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(this,"Ошибка записи файла: " + e.toString(), Toast.LENGTH_LONG).show();
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
            //MyTV.setText("Получено " + i + " SMS");

        } else {
            // empty box, no SMS
            //MyTV.setText("Список SMS пуст");
            smsadapter.clear();
            //lvMsg.setAdapter(adapter);

        }
        Cursor close;


    }
    public void CalculateTotal(){
        float P, R;
        P = 0;
        R = 0;
        for (int i = 0; i < MyItems.get(0).MyElement.size(); i++ ){
            P = P + MyItems.get(0).MyElement.get(i).Sum;
        }
        for (int j = 1; j < MyItems.size(); j++ ) {
            for (int i = 0; i < MyItems.get(j).MyElement.size(); i++) {
                R = R + MyItems.get(j).MyElement.get(i).Sum;
            }
        }
        BigDecimal d = new BigDecimal(String.valueOf(P));
        d = d.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        P = d.floatValue();
        BigDecimal d2 = new BigDecimal(String.valueOf(R));
        d2 = d2.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        R = d2.floatValue();
        Total = P - R;
        BigDecimal d3 = new BigDecimal(String.valueOf(Total));
        d3 = d3.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Total = d3.floatValue();
        //MyTV.setText(P + "\r\n" + R + "\r\n" + Total);
        TVBalance1.setText(P + "");
        TVBalance2.setText("-" + R);
        TVBalance3.setText(Total + "");
        //MyTV3.setText("Приход: " + "\r\n" + "Расход: " + "\r\n" + "Итого: ");

    }

    @Override
    public void onDestroy(){

        SaveData();
        super.onDestroy();

    }


    @Override
    public void returnDate(int year, int month, String date) {
        cur_month = month + 1;
        cur_year = year;
        ReqDate = date;
        SimpleDateFormat df = new SimpleDateFormat("MM.yyyy");
        Date convertedDate = new Date();
        try {
            convertedDate = df.parse(ReqDate);
        } catch (ParseException e) {
        }
        df = new SimpleDateFormat("MMM.yyyy");
        MyTV2.setText(df.format(convertedDate));
        GetSMS();
        GetElements();
        UpdateMyItems();
        CalculateTotal();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo){
        LastCMVID = v.getId();
        switch (v.getId()) {
            case R.id.lvItems:
                getMenuInflater().inflate(R.menu.menu_lvitems, menu);
            break;
        }

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (LastCMVID == R.id.lvItems){
            switch (item.getItemId()){
                case  R.id.delitem:
                    //Toast.makeText(this,"Удаляем", Toast.LENGTH_LONG).show();
                    if ((!MyItems.get(info.position).Name.equals("Прочее"))&(!MyItems.get(info.position).Name.equals("Поступления"))) {
                        MyItems.remove(info.position);
                        ItemsAdapter.notifyDataSetChanged();
                    } else {Toast.makeText(this,"Удаление служебной группы не возможно.", Toast.LENGTH_LONG).show();}

                    return true;
                case R.id.item:
                    ItemPosition = info.position;
                    Intent intent = new Intent(MainActivity.this, MyItemActivity.class);
                    startActivity(intent);
                    return true;
                case  R.id.rename:
                    if ((!MyItems.get(info.position).Name.equals("Прочее"))&(!MyItems.get(info.position).Name.equals("Поступления"))) {
                        ItemPosition = info.position;
                        ItemsNameDialog.show(getFragmentManager(), "");
                    }else {Toast.makeText(this,"Преименование служебной группы не возможно.", Toast.LENGTH_LONG).show();}
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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
