package com.mavroo.dialer;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.telecom.TelecomManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

//import com.terrakok.phonematter.PhoneFormat;

public class MainActivity extends AppCompatActivity{

    private static final String DEBUG_TAG = "MYAPP";
    private static final int CODE_REQUEST_SET_DEFAULT_DIALER = 1;

    EditText editTextDialPadCall;
    CallManager callManager;
    CallLogManager callLogManager;
    DialpadManager dialpadManager;
    CallLogHelper callLogHelper;
    StringManager stringManager;
    RecyclerView callLogRecyclerView;
    CallLogCursorAdapter callLogAdapter;
    Cursor callLogsCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(com.mavroo.dialer.R.layout.activity_main);

        editTextDialPadCall = findViewById(com.mavroo.dialer.R.id.dial_pad_call);
        dialpadManager = new DialpadManager(this);
        callLogManager = new CallLogManager(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            // DeviceHelper.setLightNavigationBar(this);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setDefault();
//            DeviceHelper.setLightStatusBar(this);
        }

        callManager = CallManager.getInstance();
        callLogHelper = CallLogHelper.getInstance();
        stringManager = StringManager.getInstance();
    }

    @Override
    public void onBackPressed() {
        switch (dialpadManager.getDialpadSheet().getState()) {
            case BottomSheetBehavior.STATE_EXPANDED:
                dialpadManager.closeDialpad();
                break;

            default:
                super.onBackPressed();
                break;
        }
    }

    public void onClickEditTextDialNum(View view) {
        dialpadManager.openDialpad();
    }

    public void onClickDialNum(View view) {
        Button b = (Button) view;

        /*Animation animation;
        animation = AnimationUtils.loadAnimation(this, R.anim.click_dial_num);
        animation.setFillAfter(true);
        b.startAnimation(animation);*/

        animateButtonDialNum(b);
        animateButtonCall();

        dialpadManager.addNum(b.getText().toString());
    }

    private void animateButtonDialNum(Button b) {
        final int colorFrom = getResources().getColor(android.R.color.black);
        final int colorTo = getResources().getColor(R.color.colorOne);
        PropertyValuesHolder valuesTextSizeStart = PropertyValuesHolder.ofFloat("textSize", 40, 20);
        PropertyValuesHolder valuesTextColorStart = PropertyValuesHolder.ofObject("textColor", new ArgbEvaluator(), colorFrom, colorTo);

        PropertyValuesHolder valuesTextSizeEnd = PropertyValuesHolder.ofFloat("textSize", 20, 35);
        PropertyValuesHolder valuesTextColorEnd = PropertyValuesHolder.ofObject("textColor", new ArgbEvaluator(), colorTo, colorFrom);

        ValueAnimator animator1 = ObjectAnimator.ofPropertyValuesHolder(b, valuesTextSizeStart, valuesTextColorStart);
        animator1.setDuration(170);
        animator1.start();

        ValueAnimator animator2 = ObjectAnimator.ofPropertyValuesHolder(b, valuesTextSizeEnd, valuesTextColorEnd);
        animator2.setDuration(250);
        animator2.start();
    }

    private void animateButtonCall() {
        LinearLayout dialCallButton = findViewById(R.id.dial_call);
        AnimationHelper.animateJump(this, dialCallButton);
    }

    public void onCall(View view) {
        dialpadManager.makeCall(null);
    }

    public void onAction(View view) {
        dialpadManager.openDialpad();
    }

    @Override
    protected void onResume() {
        super.onResume();

        callLogManager.loadCallLogs();
    }

    private void setDefault() {
        if(isDefault())
            return;

        final Intent intent = new Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER);
        intent.putExtra(TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME, getPackageName());
        startActivityForResult(intent, CODE_REQUEST_SET_DEFAULT_DIALER);
    }

    private boolean isDefault() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        TelecomManager telecomManager = (TelecomManager) getSystemService(TELECOM_SERVICE);
        String defaultApp = null;
        if (telecomManager != null) {
            defaultApp = telecomManager.getDefaultDialerPackage();
        }

        return getPackageName().equals(defaultApp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_REQUEST_SET_DEFAULT_DIALER) {
            if (resultCode == RESULT_OK) {
                showToast("User accepted request to become default dialer");
            } else if (resultCode == RESULT_CANCELED) {
                showToast("User declined request to become default dialer");
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void onClear(View view) {
        dialpadManager.clearDialNumber();
    }
}
