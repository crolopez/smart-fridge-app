package com.crolopez.smartfridge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class Setting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static String TAG = "SETTING";
    private static SharedPreferences preferences = null;
    private static final String PREF_H = "key_host";
    private static final String PREF_POR="key_port";
    private static final String PREF_DEF_PLACE = "key_default_place";
    private static final String PREF_ZOOM = "key_zoom";
    private static final String PREF_GEO = "key_geolocation";
    private static final String PREF_ZOOM_INC = "key_zoom_inc";
    private static final String PREF_MAP_TYPE = "key_map_type_list";
    private static final String PREF_MAP_MIN_D = "key_map_min_distance";
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

    private void initValues() {
        preferences = getPreferenceManager().getSharedPreferences();

        findPreference(PREF_H).setSummary(preferences.getString(PREF_H, null));
        findPreference(PREF_POR).setSummary(preferences.getString(PREF_POR, null));
        findPreference(PREF_DEF_PLACE).setSummary(preferences.getString(PREF_DEF_PLACE, null));
        findPreference(PREF_ZOOM).setSummary(preferences.getString(PREF_ZOOM, null));
        findPreference(PREF_ZOOM_INC).setSummary(preferences.getString(PREF_ZOOM_INC, null));
        findPreference(PREF_MAP_TYPE).setSummary(preferences.getString(PREF_MAP_TYPE, null));
        findPreference(PREF_MAP_MIN_D).setSummary(preferences.getString(PREF_MAP_MIN_D, null));
        check_geolocation = (SwitchPreference) findPreference(PREF_GEO);
    }

    public static String getServerHost(){ return preferences.getString(PREF_H, null); }
    public static int getServerPort() { return Integer.parseInt(preferences.getString(PREF_POR, null)); }
    public static String getDefaultPlace(){ return preferences.getString(PREF_DEF_PLACE, null); }
    public static float getDefaultZoom() { return Float.parseFloat(preferences.getString(PREF_ZOOM, null)); }
    public static float getDefaultZoomInc() { return Float.parseFloat(preferences.getString(PREF_ZOOM_INC, null)); }
    public static boolean getGeolocation() { return preferences.getBoolean(PREF_GEO, false); }
    public static double getNotifyDistance() { return Double.parseDouble(preferences.getString(PREF_MAP_MIN_D, null)); }
    public static void setGeolocation() { check_geolocation.setChecked(false); }
    public static int getMapType() {
        int retval;
        String value = preferences.getString(PREF_MAP_TYPE, "");

        if (value.equals("Satellite")) {
            retval = 2;
        } else if (value.equals("Terrain")) {
            retval = 3;
        } else if (value.equals("Hybrid")) {
            retval = 4;
        } else {
            retval = 1;
        }

        return retval;
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
