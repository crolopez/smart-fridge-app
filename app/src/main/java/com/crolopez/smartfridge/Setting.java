package com.crolopez.smartfridge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

public class Setting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREF_H = "host", PREF_POR="port",
            PREF_INMA="inactivityMax", PREF_APWT="appWaitTime",
            PREF_LS="lightSignal", PREF_CDR="countdownReset";

    private static SharedPreferences preferences = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(com.crolopez.smartfridge.R.xml.preferences);
        initValues();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);
        if(key.equals(PREF_CDR) || key.equals(PREF_LS)  ) {
            //pref.setSummary((preferences.getBoolean(key, false)) ? "true" : "false");
        }
        else
            pref.setSummary(sharedPreferences.getString(key,"null"));
    }

    private void initValues(){
        preferences = getPreferenceManager().getSharedPreferences();
        findPreference(PREF_H).setSummary(preferences.getString(PREF_H, null));
        findPreference(PREF_POR).setSummary(preferences.getString(PREF_POR, null));
        findPreference(PREF_APWT).setSummary(preferences.getString(PREF_APWT, null));
        findPreference(PREF_INMA).setSummary(preferences.getString(PREF_INMA, null));
    }

    public static String getServerHost(){
        return preferences.getString(PREF_H, null);
    }

    public static int getServerPort(){
        return Integer.parseInt(preferences.getString(PREF_POR, null));
    }

    public static String getInactivityMax(){
        return preferences.getString(PREF_INMA, null);
    }

    public static String getAppWait(){
        return preferences.getString(PREF_APWT, null);
    }

    public static boolean getCountdownReset(){
        return preferences.getBoolean(PREF_CDR, false);
    }

    public static boolean getLightSignal() {
        return preferences.getBoolean(PREF_LS, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
