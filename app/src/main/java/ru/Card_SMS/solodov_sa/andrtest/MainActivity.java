package ru.Card_SMS.solodov_sa.andrtest;

import android.Manifest;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity implements DatePickerFragment.TheListener {

    TextView TVBalance1, TVBalance2, TVBalance3;
    private Button  myBtn3;
    float Sum, Total, Balance;

    static DecimalFormat TwoDecFormat;

    ArrayAdapter<String> smsadapter;
    String Sender, ReqDate, TxtMask, FilePathSettings, FilePathData;
    static String TAG;

    static String Card = "visa";
    static String SMS_Permission = Manifest.permission.READ_SMS;
    static String Storage_Permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    static String strReceipts = "Поступления";
    static String strOther = "Прочее";
    static String strBalance = "Баланс";
    static String strNoBalance = "Нет информации";
    static String curenncy ="";

    static String [] arrayCurenncyData = {" " + Html.fromHtml("&#x20bd") + "  - Рубль", " \u20ac" + " - Euro", " $" + " - USD"};
    static String [] arrayCurenncy = {" " + Html.fromHtml("&#x20bd"), " \u20ac", " $"};

    int LastCMVID;

    static int curenncyPosition = 0;

    TextView lblMsg, lblNo;
    ListView lvMsg,lvItems;

    private int cur_year, cur_month, cur_day;

    public static ArrayList<msgData> MsgData = new ArrayList<>();

    static ArrayList<MyItem> MyItems = new ArrayList<MyItem>();
    static myItemsAdapter ItemsAdapter;

    DialogFragment ItemsNameDialog;

    static int ItemPosition, ElemPosition;
    static  boolean NewItem, SettingsHideNullItem, settingsShowCurenncy;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1421, STORAGE_PERMISSION_REQUEST_CODE = 4431, PERMISSION_REQUEST_CODE = 9624;

    public static int SpendTextColour = Resources.getSystem().getColor(android.R.color.holo_red_dark);
    public static int ReceiptsTextColour =  Resources.getSystem().getColor(android.R.color.holo_blue_light);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setExponentSeparator(" ");
        TwoDecFormat = new DecimalFormat("0.00");
        TwoDecFormat.setGroupingSize(3);
        TwoDecFormat.setGroupingUsed(true);
        TwoDecFormat.setDecimalFormatSymbols(dfs);

        curenncy = arrayCurenncy[curenncyPosition];

        SettingsHideNullItem = true;
        settingsShowCurenncy = true;

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

        TAG = "MyLog";
        Total  = 0;

        registerForContextMenu(lvItems);

        MsgData.clear();
        //smsadapter = new ArrayAdapter<String>(this, R.layout.sms_list, MsgData);
        //lvMsg = (ListView) findViewById(R.id.lvMsg);
        //lvMsg.setAdapter(smsadapter);

        TVBalance1 = (TextView) findViewById(R.id.textView);
        TVBalance2 = (TextView) findViewById(R.id.textView5);
        TVBalance3 = (TextView) findViewById(R.id.textView6);
        Button myBtn = (Button) findViewById(R.id.button);
        Button myBtn2 = (Button) findViewById(R.id.button2);
        myBtn3 = (Button) findViewById(R.id.button3);

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


        FilePathSettings = getExternalFilesDir(null).toString()+ "/Settings.txt" ;
        FilePathData = getExternalFilesDir(null).toString()+ "/Data.txt" ;


        df = new SimpleDateFormat("MMM.yyyy");
        myBtn3.setText(df.format(c.getTime()));

        //Читаем файл настроек
        ReadData();
        //Получить список СМС
        if (hasPermissions(SMS_Permission)){
            GetSMS();
        } else {
            requestPermissionWithRationale();
        }
        ItemOther();
        if (Card != null) {
            GetElements();
        }
        UpdateMyItems();
        CalculateTotal();
        // MyTV.setText(FilePathSettings);


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
            if (MyItems.get(i).Name.equals(strOther)) {
                f = true;
            }
            if (MyItems.get(i).Name.equals(strReceipts)) {
                f2 = true;
            }
        }
        if (!f) {
            AddMyItem(MyItems.size(),strOther);
        }
        if (!f2) {
            AddMyItem(0,strReceipts);
        }

    }

    //Поиск всех объектов element из SMS
    public void GetElements(){
        String str, str2;
        int k, l, elemNum;
        boolean f;
        elemNum = 0;
        //Log.d(TAG, "Получение элементов из СМС");
        for (int i = 0; i < MsgData.size(); i++) {
            str = MsgData.get(i).Body;
            str2 = str;
            str = str.toLowerCase();
            //Log.d(TAG, "---------");
            //Log.d(TAG, "Обрабатываем СМС");
            //Log.d(TAG, "---------");
            //Log.d(TAG, i + ": " + str);
            //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            if ((str.indexOf("покупка") > 0) && (str.indexOf("отказ") < 0)) {
                str2 = str2.substring(str.indexOf("покупка") + 8);
                str = str2.toLowerCase();
                Log.d(TAG, "<><><><><><>");
                Log.d(TAG, str2);
                Log.d(TAG, str);
                str2 = str2.substring(str.indexOf("р ")+2);
                Log.d(TAG, str2);
                //MyTV.setText(MyTV.getText() + " -- " + str);
                str = str2.toLowerCase();
                str2 = str2.substring(0,str.indexOf("баланс")-1);
                //MyTV.setText(MyTV.getText() + " -- " + str);
                //MyTV.setText(str);
                f = false;
                for (int n = 0; n < MyItems.size(); n++) {
                    for (int j = 0; j < MyItems.get(n).MyElement.size(); j++) {
                        if (str2.equals(MyItems.get(n).MyElement.get(j).Mask)) {
                            f = true;
                            //MyTV.setText(str + " " + MyItems.size());
                        }
                    }
                }
                if (!f) {
                    AddMyElem(str2, MyItems.size()-1);
                    elemNum ++;
                }

            }
            if (str.indexOf("списание")>0){
                str2 = "списание";
                str = str2.toLowerCase();
                f = false;
                for (int n = 0; n < MyItems.size(); n++) {
                    for (int j = 0; j < MyItems.get(n).MyElement.size(); j++) {
                        if (str.equals(MyItems.get(n).MyElement.get(j).Mask)) {
                            f = true;
                        }
                    }
                }
                if (!f) {
                    AddMyElem(str2, MyItems.size()-1);
                    elemNum ++;
                }
            }
            if (str.indexOf("выдача наличных") > 0) {
                str2 = str2.substring(str.indexOf("выдача наличных"),str.indexOf("выдача наличных") + 15);
                str = str2.toLowerCase();
                f = false;
                for (int n = 0; n < MyItems.size(); n++) {
                    for (int j = 0; j < MyItems.get(n).MyElement.size(); j++) {
                        if (str.equals(MyItems.get(n).MyElement.get(j).Mask)) {
                            f = true;
                        }
                    }
                }
                if (!f) {
                    AddMyElem(str2, MyItems.size()-1);
                    elemNum ++;
                }
            }
            if ((str.indexOf("оплата") > 0) && (str.indexOf("отказ") < 0)){

                Log.d(TAG, "---+++---+++---");
                Log.d(TAG, "Оплата: " + str);
                str2 = str.substring(str.indexOf("оплата") + 6);
                Log.d(TAG, "Оплата: " + str2);
                str2 = str2.substring(str2.indexOf("р ")+2);
                Log.d(TAG, "Оплата: " + str2);
                str = str2.toLowerCase();
                if (str.indexOf("баланс") > 0) {
                    str2 = str2.substring(0, str.indexOf("баланс") - 1);
                } else {
                    str2 = "оплата";
                }
                Log.d(TAG, "Оплата: " + str2);
                str = str2.toLowerCase();
                f = false;
                for (int n = 0; n < MyItems.size(); n++) {
                    for (int j = 0; j < MyItems.get(n).MyElement.size(); j++) {
                        if (str.equals(MyItems.get(n).MyElement.get(j).Mask)) {
                            f = true;
                        }
                    }
                }
                if (!f) {
                    str2 = str;
                    AddMyElem(str2, MyItems.size()-1);
                    elemNum ++;
                }
            }
            if (str.indexOf("зачисление") > 0) {
                //str2 = str2.substring(str2.indexOf("зачисление") + 11);
                //MyTV.setText(str2);
                //Log.d(TAG, "Нашли зачисление");
                k = str.indexOf("р ") + 2;
                l = str.indexOf("баланс");
                if ((l - k) > 1) {
                    str2.substring(k, l);
                } else {
                    str2 = str2.substring(str.indexOf("зачисление"), k-1);
                    l = str2.lastIndexOf(" ");
                    str2 = str2.substring(0, l);
                    //Log.d(TAG, str2);
                }
                str = str2.toLowerCase();
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
                    elemNum ++;
                }
            }
            MsgData.get(i).Mask = str2;
            MsgData.get(i).Sum = GetMsgSum(i);
            Log.d(TAG, MsgData.get(i).Mask + "  :    " + MsgData.get(i).Sum);
        }
        Log.d(TAG, "-------------------");
        Log.d(TAG, "Добавлено элементов: " + elemNum);
    }
    //Находим сумму в сообщении и возвращаем ее
    public static float GetMsgSum(int i) {
        String str;
        int k,l;
        float Sum;
        boolean Error;
        //Log.d(TAG, "---------");
        //Log.d(TAG, "GetMsgSum " + i);
        //Log.d(TAG, "---------");
        Sum = 0.0F;
        str = MsgData.get(i).Body;
        str = str.toLowerCase();
        k = str.indexOf("покупка");
        if (k > -1) {
            l = str.indexOf("р ");
            k = str.substring(0 ,l).lastIndexOf(" ");
            //Log.d(TAG, MsgData.get(i).Body);
            //Log.d(TAG,"Покупка: " + MsgData.get(i).Mask + " : " + str.substring(k, l));
            Sum = Float.parseFloat(str.substring(k, l));
        }
        k = str.indexOf("списание");
        if (k > -1) {
            l = str.indexOf("р ");
            Log.d(TAG, str);
            k = str.substring(0, l).lastIndexOf(" ");
            //Log.d(TAG, MsgData.get(i).Body);
            //Log.d(TAG, "Выдача наличных: " + MsgData.get(i).Mask + " : " + str.substring(k, l));
            Sum = Float.parseFloat(str.substring(k, l));
        }
        k = str.indexOf("выдача наличных");
        if (k > -1) {
            l = str.indexOf("р ");
            k = str.substring(0 ,l).lastIndexOf(" ");
            //Log.d(TAG, MsgData.get(i).Body);
            //Log.d(TAG, "Выдача наличных: " + MsgData.get(i).Mask + " : " + str.substring(k, l));
            Sum = Float.parseFloat(str.substring(k, l));
        }
        k = str.indexOf("оплата");
        if ((k > -1) && (str.indexOf("отказ") < 0)){
            //k = k + ;
            //l = str.indexOf("р ");
            //Log.d(TAG, MsgData.get(i).Body);
            //Log.d(TAG, "Оплата услуг: " + MsgData.get(i).Mask + " : " + str.substring(k, l));
            //Sum = Float.parseFloat(str.substring(k, l));
            Log.d(TAG, str);
            str = str.substring(k+7);
            Error = false;
            try {
                str = str.substring(0, str.lastIndexOf("баланс")-1);
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
                Error = true;
            }
            if (!Error) {
                Pattern myPatter = Pattern.compile("(\\d*+\\.)?+\\d+(р)"); //Задаем паттерн для поиска числа целого/дробного с бувой "р" на конце.
                Matcher myMatcher = myPatter.matcher(str);
                while (myMatcher.find()) {                                 //Ищем вхождения паттерна в строку.
                    str = myMatcher.group();
                    Sum = Float.parseFloat(str.substring(0, str.length() - 1));
                    Log.d(TAG, "----------->" + Sum);
                }
            }
        }
        k = str.indexOf("зачисление");
        if (k > -1) {
            l = str.indexOf("р ");
            k = str.substring(0 ,l).lastIndexOf(" ");
            //Log.d(TAG, MsgData.get(i).Body);
            //Log.d(TAG, "Зачисление: " + MsgData.get(i).Mask + " : " + str.substring(k, l));
            Sum = Float.parseFloat(str.substring(k, l));
            //str2 = str.substring(k, l);
        }
        BigDecimal d = new BigDecimal(String.valueOf(Sum));
        d = d.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        Sum = d.floatValue();
        return Sum;
    }

    //Обновление информации MyItems из SMS
    public static void UpdateMyItems(){
        String str, TxtMask;
        int k, l, num;
        float Sum;
        Log.d(TAG, "Обновление элементов");

        for (int i = 0; i < MyItems.size(); i++) {

            for (int n = 0; n < MyItems.get(i).MyElement.size(); n++) {
                Sum = 0;
                num = 0;
                TxtMask = MyItems.get(i).MyElement.get(n).Mask;
                //TxtMask = TxtMask.toLowerCase();
                for (int j = 0; j < MsgData.size(); j++) {

                    str = MsgData.get(j).Mask;
                    //str = str.toLowerCase();

                    //Log.d(TAG, "-------------------");
                    //Log.d(TAG, TxtMask + " : " + str);

                    if (str.equals(TxtMask)) {
                        num ++;
                        Sum += MsgData.get(j).Sum;
                    }
                }
                Log.d(TAG, "-------------------");
                Log.d(TAG, MyItems.get(i).MyElement.get(n).Mask + ": " + num + " : " + Sum);
                MyItems.get(i).MyElement.get(n).SmsCount = num;
                MyItems.get(i).MyElement.get(n).Sum = Sum;
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
        Log.d(TAG, "---------");
        Log.d(TAG, "Добавлен элемент: " + Mask);
        Log.d(TAG, "---------");

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
            for (int i = 0; i < MsgData.size(); i++) {
                str = MsgData.get(i).Body;
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
        Log.d(TAG, "ReadSettings Start");
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show();
            return;
        } else {
            File fhandle = new File(FilePathSettings);
            try {
                FileInputStream inStream = new FileInputStream(fhandle);

                if ( inStream != null ) {
                    MyItems.clear();
                    InputStreamReader inputStreamReader = new InputStreamReader(inStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    //StringBuilder stringBuilder = new StringBuilder();
                        Log.d(TAG, "Чтение первой строки из файла");
                        try {
                            Card = bufferedReader.readLine();
                        } catch (Exception e){
                            Log.e(TAG, "Ошибка: " + e.toString());
                            Card = "visa";
                        }
                        Log.d(TAG, "Card: " + Card);
                        try {
                            SettingsHideNullItem = Boolean.parseBoolean(bufferedReader.readLine());
                        } catch (Exception e){
                            Log.e(TAG, "Ошибка: " + e.toString());
                            SettingsHideNullItem = true;
                        }
                        Log.d(TAG, "SettingsHideNullItem: " + SettingsHideNullItem);
                        try {
                            settingsShowCurenncy = Boolean.parseBoolean(bufferedReader.readLine());
                        } catch (Exception e){
                            Log.e(TAG, "Ошибка: " + e.toString());
                            settingsShowCurenncy = false;
                        }
                        Log.d(TAG, "settingsShowCurenncy: " + settingsShowCurenncy );
                        try {
                            curenncyPosition = Integer.parseInt(bufferedReader.readLine());
                        } catch (Exception e){
                            Log.e(TAG, "Ошибка: " + e.toString());
                            curenncyPosition = 0;
                        }
                        Log.d(TAG, "curenncyPosition: " + curenncyPosition);
                        SetCurenncy();

                    inStream.close();
                    //ret = stringBuilder.toString();
                }

            }
            catch (FileNotFoundException e) {
                //Log.e("login activity", "File not found: " + e.toString());
                Toast.makeText(this, "Файл с настройками не найден: " + e.toString(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                //Log.e("login activity", "Can not read file: " + e.toString());
                Toast.makeText(this, "Ошибка чтения файла: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
         Log.d(TAG, "ReadData Start");
         if (!Environment.getExternalStorageState().equals(
                 Environment.MEDIA_MOUNTED)) {
             Toast.makeText(this, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show();
             return;
         } else {
             File fhandle = new File(FilePathData);
             try {
                 FileInputStream inStream = new FileInputStream(fhandle);

                 if ( inStream != null ) {
                     MyItems.clear();
                     InputStreamReader inputStreamReader = new InputStreamReader(inStream);
                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                     String receiveString = "";
                     while ( (receiveString = bufferedReader.readLine()) != null ) {
                         //stringBuilder.append(receiveString);
                         int num = Integer.parseInt(receiveString);
                         Log.d(TAG, "Прочитали: " + num);
                         if (num != 0){
                             for (int i = 0; i <= num-1; i++) {
                                 String name = bufferedReader.readLine();
                                 AddMyItem(MyItems.size(),name);
                                 // int SmsCount = Integer.parseInt(bufferedReader.readLine().toString());
                                 // Float sum = Float.parseFloat(bufferedReader.readLine().toString());
                                 //ArrayList<String> MA = new ArrayList<String>();
                                 int num2 = Integer.parseInt(bufferedReader.readLine());
                                 for (int j = 0; j <= num2-1; j++) {
                                     String Mask = bufferedReader.readLine();
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
             } catch (IOException e) {
                 //Log.e("login activity", "Can not read file: " + e.toString());
                 Toast.makeText(this, "Ошибка чтения файла: " + e.toString(), Toast.LENGTH_LONG).show();
             }
         }
        Log.d(TAG, "ReadData End");
    }
    //Запись файла с настройками
    private void SaveData() {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show();
            return;
        } else {

            File fhandle = new File(FilePathSettings);
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
                myOutWriter.write(Card + LS);
                myOutWriter.write(SettingsHideNullItem + LS);
                myOutWriter.write(settingsShowCurenncy + LS);
                myOutWriter.write(curenncyPosition + LS);
                myOutWriter.close();
                fOut.close();

                //Toast.makeText(this, "Файл записан на SD-карту" + FilePathSettings, Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                //Toast.makeText(this,"Path " + FilePathSettings + ", " + e.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(this,"Ошибка записи файла: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD-карта не доступна: " + Environment.getExternalStorageState(), Toast.LENGTH_LONG).show();
            return;
        } else {

            File fhandle = new File(FilePathData);
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
                    // myOutWriter.write(LS + MyItems.get(i).SmsCount);
                    // myOutWriter.write(LS + String.valueOf(MyItems.get(i).Sum));
                    myOutWriter.write(LS + String.valueOf(MyItems.get(i).MyElement.size()));
                    for (int j = 0; j < MyItems.get(i).MyElement.size(); j++){
                        myOutWriter.write(LS + MyItems.get(i).MyElement.get(j).Mask);
                    }
                }
                myOutWriter.close();
                fOut.close();

                //Toast.makeText(this, "Файл записан на SD-карту" + FilePathSettings, Toast.LENGTH_LONG).show();
            }
            catch (IOException e)
            {
                //e.printStackTrace();
                //Toast.makeText(this,"Path " + FilePathSettings + ", " + e.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(this,"Ошибка записи файла: " + e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void GetSMS() {
        Log.d(TAG,"++++++++++++++++++++ GetSMS Start");
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);
        MsgData.clear();
        int i = 0;
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            String SmsDate = "";
            // String TxtBody = "";
            //smsadapter.clear();
            //Toast.makeText(this,cursor.getString(2),Toast.LENGTH_SHORT).show();
            do {
                SmsDate = new SimpleDateFormat("MM.yyyy").format(cursor.getLong(5));
                if (Sender.equals(cursor.getString(2)) && SmsDate.equals(ReqDate))   {
                    //smsadapter.add("Дата:" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cursor.getLong(5)) + "; От:" + cursor.getString(2) + "; Текст:" + cursor.getString(13));
                    msgData MData = new msgData((cursor.getString(cursor.getColumnIndexOrThrow("body"))), "", 0F, new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cursor.getLong(5)));
                    //MsgData.add("Дата:" + new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cursor.getLong(5)) + "; От:" + cursor.getString(2) + "; Текст:" + cursor.getString(cursor.getColumnIndexOrThrow("body")));
                    String MsgBody = MData.Body.substring(0,Card.length());
                    MsgBody = MsgBody.toLowerCase();
                    Card = Card.toLowerCase();
                    //Log.d(TAG, MsgBody);
                    if (Card.equals(MsgBody) & !Card.equals("visa")){
                        Log.d(TAG, MData.Body);
                        MsgData.add(MData);
                        i++;
                    }
                    //MyItems.add(new MyItem("Test11111", i, i+i ));
                    //Log.d(TAG, MData.Date + "   " + MData.Mask + "   " + MData.Body);
                }
                //for(int idx=0;idx<cursor.getColumnCount();idx++)
                //{
                //    msgData = new String[] {cursor.getColumnName(idx) + ":" + cursor.getString(idx)};
                //}
                // use msgData
                //msgData = new String[] {"Дата:" + cursor.getString(5) + "; От:" + cursor.getString(2) + "; Текст:" + cursor.getString(13)};
                //MyTV.setText(msgData);

            } while (cursor.moveToNext());
            Toast.makeText(this,"Получено СМС: " + i,Toast.LENGTH_SHORT).show();
            GetBalnce();
            //MyTV.setText("Получено " + i + " SMS");

        } else {
            // empty box, no SMS
            //MyTV.setText("Список SMS пуст");
            //smsadapter.clear();
            //lvMsg.setAdapter(adapter);

        }
        Cursor close;
        Log.d(TAG,"GetSMS Получено SMS: " + i);
        Log.d(TAG,"-------------------- GetSMS End");

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
        TVBalance1.setText(TwoDecFormat.format(P) + "");
        TVBalance2.setText("-" + TwoDecFormat.format(R));
        TVBalance3.setText(TwoDecFormat.format(Total) + "");
        if (Total < 0){
            TVBalance3.setTextColor(SpendTextColour);
        } else {
            TVBalance3.setTextColor(ReceiptsTextColour);
        }

    }

    @Override
    public void onDestroy(){

        SaveData();
        super.onDestroy();

    }
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
        myBtn3.setText(df.format(convertedDate));
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
                break;
            case R.id.file_save:
                SaveData();
                break;
            case R.id.file_read:
                ReadData();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean hasPermissions(String Perm){
        int res = 0;
        res = checkCallingOrSelfPermission(Perm);
        if (!(res == PackageManager.PERMISSION_GRANTED)){
            return false;
        }
        return true;
    }
    public void requestPermissionWithRationale() {
        /*if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(MainActivity.this.findViewById(R.id.activity_view), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }*/
        requestPerms();
    }
    private void requestPerms(){
        String[] permissions = new String[]{SMS_Permission, Storage_Permission};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
    }

    public void GetBalnce(){
        String SMSBody;
        int i=0, k;
        SMSBody = strNoBalance;
        while ((i >= 0)&&(i < MsgData.size())) {
            SMSBody = MsgData.get(i).Body;
            k = SMSBody.indexOf(strBalance);
            if (k > 0){
                SMSBody = SMSBody.substring(SMSBody.lastIndexOf(strBalance) + 8, SMSBody.lastIndexOf("р"));
                i = -1;
            } else {
                i++;
                if (i == MsgData.size()) {
                    i = -1;
                    SMSBody = strNoBalance;
                }
            }
        }
        setTitle(SMSBody);
    }

    public static void SetCurenncy(){
        if (settingsShowCurenncy){
            curenncy = arrayCurenncy[curenncyPosition];
        } else {
            curenncy = "";
        }
    }
}
