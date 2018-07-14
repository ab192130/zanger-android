package com.mavroo.dialer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class InputPhoneNumberDialogTwo extends DialogFragment {
    private int requestCode;

    private FloatingActionButton buttonSave;
    private FloatingActionButton buttonDismiss;

    private Button buttonNumOne;
    private Button buttonNumTwo;
    private Button buttonNumThree;
    private Button buttonNumFour;
    private Button buttonNumFive;
    private Button buttonNumSix;
    private Button buttonNumSeven;
    private Button buttonNumEight;
    private Button buttonNumNine;
    private Button buttonNumStar;
    private Button buttonNumZero;
    private Button buttonNumSheet;

    EditText editInputNum;
    OnPhoneNumberInputListener mListener;

    public interface OnPhoneNumberInputListener{
        void onApplyInputPhoneNumber(@Nullable Bundle data, int requestCode);
    }

    public void setRequestCode(int code) {
        this.requestCode = code;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.input_phone_number, container, false);

        buttonSave    = view.findViewById(R.id.button_save);
        buttonDismiss = view.findViewById(R.id.button_cancel);

        editInputNum = view.findViewById(R.id.input_num_edit);

        buttonNumOne   = view.findViewById(R.id.input_num_one);
        buttonNumTwo   = view.findViewById(R.id.input_num_two);
        buttonNumThree = view.findViewById(R.id.input_num_three);
        buttonNumFour  = view.findViewById(R.id.input_num_four);
        buttonNumFive  = view.findViewById(R.id.input_num_five);
        buttonNumSix   = view.findViewById(R.id.input_num_six);
        buttonNumSeven = view.findViewById(R.id.input_num_seven);
        buttonNumEight = view.findViewById(R.id.input_num_eight);
        buttonNumNine  = view.findViewById(R.id.input_num_nine);
        buttonNumStar  = view.findViewById(R.id.input_num_star);
        buttonNumZero  = view.findViewById(R.id.input_num_zero);
        buttonNumSheet = view.findViewById(R.id.input_num_sheet);

        buttonNumOne.setOnClickListener(new OnClickListener());
        buttonNumTwo.setOnClickListener(new OnClickListener());
        buttonNumThree.setOnClickListener(new OnClickListener());
        buttonNumFour.setOnClickListener(new OnClickListener());
        buttonNumFive.setOnClickListener(new OnClickListener());
        buttonNumSix.setOnClickListener(new OnClickListener());
        buttonNumSeven.setOnClickListener(new OnClickListener());
        buttonNumEight.setOnClickListener(new OnClickListener());
        buttonNumNine.setOnClickListener(new OnClickListener());
        buttonNumStar.setOnClickListener(new OnClickListener());
        buttonNumZero.setOnClickListener(new OnClickListener());
        buttonNumSheet.setOnClickListener(new OnClickListener());

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle data = new Bundle();
                data.putString("phone_number", editInputNum.getText().toString());

                mListener.onApplyInputPhoneNumber(data, requestCode);
                getDialog().dismiss();
            }
        });

        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        
        try {
            mListener = (OnPhoneNumberInputListener) getActivity();
        } catch (ClassCastException e) {
            Log.e("MYAPP", "onAttach: ClassCastException: " + e.getMessage());
        }
    }*/

    public void setOnPhoneInputListener(OnPhoneNumberInputListener phoneInputListener) {
        mListener = phoneInputListener;
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Button button = (Button) v;

            editInputNum.append(button.getText());
            editInputNum.setSelection(editInputNum.getText().length());
        }
    }
}
