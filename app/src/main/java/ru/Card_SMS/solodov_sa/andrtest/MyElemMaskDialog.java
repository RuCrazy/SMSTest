package ru.Card_SMS.solodov_sa.andrtest;

/**
 * Created by solodov_sa on 21.03.2016.
 */

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MyElemMaskDialog extends DialogFragment implements OnClickListener {

    EditText MyTextEditTxt;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Введите искомый текст");
        View v = inflater.inflate(R.layout.elemmaskdialog, null);
        v.findViewById(R.id.emOk).setOnClickListener(this);
        v.findViewById(R.id.emCancel).setOnClickListener(this);
        MyTextEditTxt = (EditText) v.findViewById(R.id.etMask);
        return v;
    }

    public void onClick(View v) {

        int id = ((Button) v).getId();
        switch (id) {
            case R.id.emOk:
                MainActivity.AddMyElem(MyTextEditTxt.getText().toString(), MainActivity.ItemPosition);
                MyTextEditTxt.setText("");
                MainActivity.UpdateMyItems();
                MyItemActivity.ItemAdapter.notifyDataSetChanged();
                break;
            case R.id.emCancel:
                //Toast.makeText(getActivity(), MyTextEditTxt.getText() + " Отмена", Toast.LENGTH_LONG).show();
        }
        dismiss();
    }
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }
}