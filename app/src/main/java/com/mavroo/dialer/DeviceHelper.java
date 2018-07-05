package com.mavroo.dialer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;

public class DeviceHelper {
    private static final DeviceHelper ourInstance = new DeviceHelper();
    private static final int DEFAULT_DURATION_VIBRATE = 35;

    public static DeviceHelper getInstance() {
        return ourInstance;
    }

    private DeviceHelper() {
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static void isDefault() {
    }

    public static void vibrate(Vibrator vibrator) {
        if(vibrator.hasVibrator()) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                vibrator.vibrate(VibrationEffect.createOneShot(DEFAULT_DURATION_VIBRATE, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(DEFAULT_DURATION_VIBRATE);
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    public static void setLightStatusBar(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().setStatusBarColor(Color.WHITE);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public static void setLightNavigationBar(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        activity.getWindow().setNavigationBarColor(Color.WHITE);
    }

    public static void setDefaultStatusBar(Activity activity) {
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.colorAndroidDarkGray));
    }
}
