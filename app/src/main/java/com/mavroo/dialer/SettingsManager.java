package com.mavroo.dialer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private Activity mActivity;
    private Context mContext;

    SettingsManager(Context context) {
        mContext = context;
        mActivity = (Activity) context;
    }

    private SharedPreferences getSharedPrefences() {
        return mActivity.getPreferences(Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return getSharedPrefences().edit();
    }

    public void save(String key, String value) {
        getEditor().putString(key, value).apply();
    }

    public void save(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    public void save(String key, boolean value) {
        getEditor().putBoolean(key, value).apply();
    }

    public String read(String key, String def) {
        return getSharedPrefences().getString(key, def);
    }

    public boolean hasSetting(String key, String def) {
        String setting = read(key, def);

        return (setting != null && !read(key, def).isEmpty());
    }

    public void remove(String key) {
        getEditor().remove(key).apply();
    }
}
