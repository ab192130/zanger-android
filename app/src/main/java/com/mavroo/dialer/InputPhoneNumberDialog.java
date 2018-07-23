package com.mavroo.dialer;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

@Deprecated
class InputPhoneNumberDialog extends Dialog{
    private static final int DIALOG_THEME_CUSTOM    = R.style.DialogThemeCustom;
    private static final int DEFAULT_LAYOUT_CONTENT = R.layout.input_phone_number;

    private final Dialog thisDialog = this;
    private FloatingActionButton buttonOk;
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
    private EditText editInputNum;

    InputPhoneNumberDialog(@NonNull Context context) {
        super(context, DIALOG_THEME_CUSTOM);

        setContentView(DEFAULT_LAYOUT_CONTENT);

        buttonOk      = this.findViewById(R.id.button_save);
        buttonDismiss = this.findViewById(R.id.button_cancel);

        editInputNum  = this.findViewById(R.id.input_num_edit);

        buttonNumOne   = this.findViewById(R.id.input_num_one);
        buttonNumTwo   = this.findViewById(R.id.input_num_two);
        buttonNumThree = this.findViewById(R.id.input_num_three);
        buttonNumFour  = this.findViewById(R.id.input_num_four);
        buttonNumFive  = this.findViewById(R.id.input_num_five);
        buttonNumSix   = this.findViewById(R.id.input_num_six);
        buttonNumSeven = this.findViewById(R.id.input_num_seven);
        buttonNumEight = this.findViewById(R.id.input_num_eight);
        buttonNumNine  = this.findViewById(R.id.input_num_nine);
        buttonNumStar  = this.findViewById(R.id.input_num_star);
        buttonNumZero  = this.findViewById(R.id.input_num_zero);
        buttonNumSheet = this.findViewById(R.id.input_num_sheet);

        buttonDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisDialog.dismiss();
            }
        });

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
