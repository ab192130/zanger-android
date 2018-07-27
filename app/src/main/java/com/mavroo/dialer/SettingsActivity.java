package com.mavroo.dialer;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    private static final int LAYOUT_FRAME_SETTINGS = R.id.layout_frame_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Fragment fragmentSettings = new SettingsFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(LAYOUT_FRAME_SETTINGS, fragmentSettings);
        transaction.commit();
    }

    public void onBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            Preference buildVersion = findPreference("pref_app_version");
            buildVersion.setSummary(BuildConfig.VERSION_NAME);
        }
    }
}
