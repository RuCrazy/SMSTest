package ru.example.solodov_sa.andrtest;

/**
 * Created by solodov_sa on 21.03.2016.
 */

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class MyItemsNameDialog extends DialogFragment implements OnClickListener {

    final String LOG_TAG = "myLogs";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Введите наименование");
        View v = inflater.inflate(R.layout.itemsnamedialog, null);
        v.findViewById(R.id.ok).setOnClickListener(this);
        v.findViewById(R.id.cancel).setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        Toast.makeText(getActivity(), "Dialog 1: " + ((Button) v).getText(),Toast.LENGTH_LONG).show();
        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

    }
}