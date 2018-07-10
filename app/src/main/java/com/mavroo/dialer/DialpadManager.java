package com.mavroo.dialer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
//import com.terrakok.phonematter.PhoneFormat;

import java.lang.reflect.Method;

class DialpadManager {
    private static final String DEFAULT_REGION = "AZ";
    private static final int    IMAGE_PLACEHOLDER_FACE = R.drawable.image_placeholder_face;
    private static final int    CODE_REQUEST_PERMISSION_PHONE_CALL = 309;

    private Context mContext;
    private Activity mActivity;
    private BottomSheetBehavior dialpadSheet;
    private ImageButton buttonDialClear;
    private EditText editTextDialpadCall;
    private TextView textContactDataShort;
    private ImageView imageViewDialpadFace;
    private ImageView imageIconDialCallButton;
    private LinearLayout dialpadLayout;
    private Button buttonDialNumZero;
    private PhoneNumberUtil phoneUtil;
    private PhoneNumberManager phoneNumberManager;
    private EditText editTextDialNum;
    private ImageButton buttonAction;
    private boolean dialpadOpened = false;
    private Vibrator vibrator;
    private VibrationEffect vibrationEffect;

    private String numberDial;

    //    private PhoneFormat phoneFormat;

    DialpadManager(Context context) {
        mContext = context;
        mActivity = (Activity) mContext;

        initDialpad();
    }

    private void initDialpad() {
        numberDial = "";

        dialpadLayout = mActivity.findViewById(R.id.bottom_sheet);
        dialpadSheet = BottomSheetBehavior.from(dialpadLayout);
        dialpadSheet.setHideable(false);
        phoneNumberManager = new PhoneNumberManager();
        vibrator = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE);

        buttonDialClear = mActivity.findViewById(R.id.dial_clear);
        buttonDialNumZero = mActivity.findViewById(R.id.dial_num_zero);
        buttonAction = mActivity.findViewById(R.id.dial_button_action);
        textContactDataShort = mActivity.findViewById(R.id.text_contact_data_short);
        editTextDialNum = mActivity.findViewById(R.id.edit_text_dial_num);
        editTextDialpadCall = mActivity.findViewById(R.id.dial_pad_call);
        imageViewDialpadFace = mActivity.findViewById(R.id.dial_image_face);
        imageIconDialCallButton = mActivity.findViewById(R.id.dial_call_button_icon);

        //phoneFormat = new PhoneFormat("az" ,mContext);
        phoneUtil = PhoneNumberUtil.getInstance();

        buttonDialClear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cancelDialNumber();
                return true;
            }
        });

        buttonDialNumZero.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                editTextDialpadCall.append("+");
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            editTextDialpadCall.setShowSoftInputOnFocus(false);
        } else {
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus", new Class[]{boolean.class});
                method.setAccessible(true);
                method.invoke(editTextDialpadCall, false);
            } catch (Exception e) {
                // ignore
            }
        }

        ConstraintLayout dialContentLayout = mActivity.findViewById(com.mavroo.dialer.R.id.dial_pad_content);
        Double newWidth = (DeviceHelper.getScreenWidth(mActivity)) * 0.65;
        Double newHeight = newWidth;
        ConstraintLayout.LayoutParams layoutParams =
                new ConstraintLayout.LayoutParams(newWidth.intValue(), newHeight.intValue());
        imageViewDialpadFace.setLayoutParams(layoutParams);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(dialContentLayout);
        constraintSet
                .connect(imageViewDialpadFace.getId(), ConstraintSet.LEFT,
                        dialContentLayout.getId(), ConstraintSet.LEFT);
        constraintSet
                .connect(imageViewDialpadFace.getId(), ConstraintSet.RIGHT,
                        dialContentLayout.getId(), ConstraintSet.RIGHT);
        constraintSet
                .connect(imageViewDialpadFace.getId(), ConstraintSet.BOTTOM,
                        com.mavroo.dialer.R.id.dial_call, ConstraintSet.BOTTOM);

        constraintSet.applyTo(dialContentLayout);

        dialpadSheet.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //editTextDialNum.setFocusable(false);
                        //editTextDialNum.setFocusableInTouchMode(false);
                        //editTextDialNum.getParent().requestLayout();

                        dialpadOpened = false;
                        showButtonAction();
                        break;

                    case BottomSheetBehavior.STATE_EXPANDED:
                        dialpadOpened = true;
                        hideButtonAction();

                        //editTextDialNum.setFocusable(true);
                        //editTextDialNum.setFocusableInTouchMode(true);
                        //editTextDialNum.getParent().requestLayout();
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;

                    case BottomSheetBehavior.STATE_SETTLING:

                        //@todo: get dialpadOpened boolean data from BottomSheet slideOffset.
                        if (!dialpadOpened) {
                            hideButtonAction();
                        } else {
                            showButtonAction();
                        }

                        break;

                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //...
            }
        });
    }

    private void hideButtonAction() {
        if (buttonAction.getVisibility() == View.GONE)
            return;

        Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_down);
        animation.setFillAfter(true);
        //animation.setInterpolator(new BounceInterpolator());

        buttonAction.startAnimation(animation);

        buttonAction.setVisibility(View.GONE);
    }

    private void showButtonAction() {
        if (buttonAction.getVisibility() == View.VISIBLE)
            return;

        buttonAction.setVisibility(View.VISIBLE);

        Animation animation;
        animation = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
        animation.setFillAfter(true);
        //animation.setInterpolator(new BounceInterpolator());
        buttonAction.startAnimation(animation);
    }

    private void cancelDialNumber() {
        editTextDialpadCall.setText("");
        resetCallActionType();

        resetDefaultContactData();
    }

    private void resetCallActionType() {
        imageIconDialCallButton.setImageResource(R.drawable.image_icon_phone_white);
    }

    private String getDialNumber() {
        return editTextDialpadCall.getText().toString();
    }

    private void setDialNumber(String phoneNumber) {
        editTextDialpadCall.setText(phoneNumber);
    }

    void clearDialNumber() {
        String newNumber = StringManager.removeLastChar(getDialNumber());

        if (newNumber == null)
            return;

        numberDial = newNumber;
        setDialNumber(newNumber);

        previewNumber();
    }

    private String resetFormatting(String number) {
        return number.replaceAll("\\s+", "");
    }

    void addNum(String number) {

        DeviceHelper.vibrate(vibrator);

        numberDial = numberDial + number;
        editTextDialpadCall.append(number);
        editTextDialpadCall.setSelection(getDialNumber().length());

        previewNumber();
    }

    private void resetDefaultContactImage() {
        imageViewDialpadFace.setImageResource(IMAGE_PLACEHOLDER_FACE);
    }

    private void resetDefaultContactData() {
        resetDefaultContactImage();
        editTextDialNum.setText("");
        editTextDialNum.setFocusable(false);

        textContactDataShort.setText("");
        textContactDataShort.setVisibility(View.INVISIBLE);

        setDialNumber(resetFormatting(getDialNumber()));
    }

    private void previewNumber() {
        if (getDialNumber().equals(""))
            return;

        if (phoneNumberManager.isRunnable(getDialNumber())) {
            showRunnableActionType();
        } else {
            resetCallActionType();
        }

        try (Cursor cursorContact = ContactHelper.getByPhoneNumber(mContext, getDialNumber())) {
            if (cursorContact.moveToFirst()) {
                String contactName = cursorContact.getString(cursorContact
                        .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                String contactPhoto = cursorContact.getString(cursorContact
                        .getColumnIndex(ContactsContract.PhoneLookup.PHOTO_URI));

                if (contactPhoto != null) {
                    imageViewDialpadFace.setImageURI(Uri.parse(contactPhoto));
                } else
                    resetDefaultContactImage();

                AnimationHelper.animateJumpLow(mContext, imageViewDialpadFace);
                formatDialNumber();

                editTextDialNum.setText(contactName);

                textContactDataShort.setText(contactName);
                textContactDataShort.setVisibility(View.VISIBLE);
            } else {
                resetDefaultContactData();
            }
        }
    }

    private void showRunnableActionType() {
        imageIconDialCallButton.setImageResource(R.drawable.image_media_play);
    }

    private void formatDialNumber() {
        String newNumber = phoneNumberManager.format(getDialNumber());
        setDialNumber(newNumber);
    }

    @Deprecated
    private Phonenumber.PhoneNumber getNumberParsed(String number) throws NumberParseException {
        if (number == null)
            number = getDialNumber();

        return phoneUtil.parse(number, DEFAULT_REGION);
    }

    void makeCall(String number) {
        if (number == null)
            number = getDialNumber();

        if(number.isEmpty())
            return;

        if(ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = new String[] {Manifest.permission.CALL_PHONE};
            ActivityCompat
                    .requestPermissions(
                            mActivity,
                            permissions,
                            CODE_REQUEST_PERMISSION_PHONE_CALL);
            return;
        }

        if(phoneNumberManager.isRunnable(number)) {
            number = number.replace("#", Uri.encode("#"));
        }

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));

        mContext.startActivity(intent);
    }

    public void openDialpad() {
        dialpadSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public void closeDialpad() {
        dialpadSheet.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public BottomSheetBehavior getDialpadSheet() {
        return dialpadSheet;
    }
}
