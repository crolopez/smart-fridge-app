package com.crolopez.smartfridge;

import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;

import static java.lang.Thread.sleep;

public class Setting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static String TAG = "SETTING";
    private static SharedPreferences preferences = null;
    private static final String PREF_H = "key_host";
    private static final String PREF_POR="key_port";
    private static final String PREF_DEF_PLACE = "key_default_place";
    private static final String PREF_ZOOM = "key_zoom";
    private static final String PREF_GEO = "key_geolocation";
    private static SwitchPreference check_geolocation;

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
        if(key.equals(PREF_GEO)) {
            if (getGeolocation()) {
                MainActivity.ask_permissions();
            }
        } else {
            pref.setSummary(sharedPreferences.getString(key, "null"));
        }
    }

    private void initValues(){
        preferences = getPreferenceManager().getSharedPreferences();

        findPreference(PREF_H).setSummary(preferences.getString(PREF_H, null));
        findPreference(PREF_POR).setSummary(preferences.getString(PREF_POR, null));
        findPreference(PREF_DEF_PLACE).setSummary(preferences.getString(PREF_DEF_PLACE, null));
        findPreference(PREF_ZOOM).setSummary(preferences.getString(PREF_ZOOM, null));
        check_geolocation = (SwitchPreference) findPreference(PREF_GEO);
    }

    public static String getServerHost(){ return preferences.getString(PREF_H, null); }
    public static int getServerPort() { return Integer.parseInt(preferences.getString(PREF_POR, null)); }
    public static String getDefaultPlace(){
        return preferences.getString(PREF_DEF_PLACE, null);
    }
    public static int getDefaultZoom() { return Integer.parseInt(preferences.getString(PREF_POR, null)); }
    public static boolean getGeolocation() { return preferences.getBoolean(PREF_GEO, false); }
    public static void setGeolocation() { check_geolocation.setChecked(false); }

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
