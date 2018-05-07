package ru.Card_SMS.solodov_sa.andrtest;

/**
 * Created by solodov_sa on 21.03.2016.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MyItemsNameDialog extends DialogFragment implements OnClickListener {

    EditText MyTextEditTxt;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Введите наименование");
        View v = inflater.inflate(R.layout.itemsnamedialog, null);
        v.findViewById(R.id.ok).setOnClickListener(this);
        v.findViewById(R.id.cancel).setOnClickListener(this);
        MyTextEditTxt = (EditText) v.findViewById(R.id.editText2);
        return v;
    }
    @Override
    public void onStart(){
        super.onStart();
        MyTextEditTxt.setText(MainActivity.MyItems.get(MainActivity.ItemPosition).Name);

    }

    public void onClick(View v) {

        int id = ((Button) v).getId();
        switch (id) {
            case R.id.ok:
                boolean eqName = false;
                for (int i = 0; i < MainActivity.MyItems.size(); i++) {
                    if (MainActivity.MyItems.get(i).Name.equals(MyTextEditTxt.getText().toString())) {
                        eqName = true;
                    }
                }
                if (!eqName) {
                    if (!MyTextEditTxt.getText().toString().equals("")) {
                        MainActivity.MyItems.get(MainActivity.ItemPosition).Name = MyTextEditTxt.getText().toString();
                        MainActivity.ItemsAdapter.notifyDataSetChanged();
                    }
                    MyTextEditTxt.setText("");
                    MainActivity.NewItem = false;
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), "Группа с имененм " + MyTextEditTxt.getText() + " уже существует", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.cancel:
                if (MainActivity.NewItem) {
                    MainActivity.MyItems.remove(MainActivity.ItemPosition);
                    MainActivity.NewItem = false;
                }
                dismiss();
                break;
        }
    }
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }
}