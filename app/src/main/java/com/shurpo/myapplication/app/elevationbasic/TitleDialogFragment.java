package com.shurpo.myapplication.app.elevationbasic;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.shurpo.myapplication.app.R;

/**
 * Created by Maksim on 10.07.2014.
 */
public class TitleDialogFragment extends DialogFragment implements View.OnClickListener{

    private EditText editText;

    private TextView yes;
    private TextView cancel;
    private Communicator communicator;

    public TitleDialogFragment(Communicator communicator) {
        this.communicator = communicator;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.elevation_dialog_view, null);
        editText = (EditText) view.findViewById(R.id.ed_title);
        yes = (TextView) view.findViewById(R.id.dialog_yes);
        cancel = (TextView) view.findViewById(R.id.dialog_cancel);
        yes.setOnClickListener(this);
        cancel.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_yes:
                communicator.onDialogMessage(editText.getText().toString());
                dismiss();
                break;
            case R.id.dialog_cancel:
                dismiss();
                break;
        }
    }

    public interface Communicator {
        public void onDialogMessage(String message);
    }
}
