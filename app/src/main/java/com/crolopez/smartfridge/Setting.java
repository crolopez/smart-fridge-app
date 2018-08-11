package com.crolopez.smartfridge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class Setting extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static SharedPreferences preferences = null;
    private static final String PREF_H = "key_host";
    private static Preference p_host;
    private static final String PREF_POR="key_port";
    private static Preference p_port;
    private static final String PREF_DEF_PLACE = "key_default_place";
    private static Preference p_place;
    private static final String PREF_ZOOM = "key_zoom";
    private static Preference p_zoom;

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

        pref.setSummary(sharedPreferences.getString(key,"null"));
    }

    private void initValues(){
        preferences = getPreferenceManager().getSharedPreferences();

        // Init references
        p_host = findPreference(PREF_H);
        p_port = findPreference(PREF_POR);
        p_place = findPreference(PREF_DEF_PLACE);
        p_zoom = findPreference(PREF_ZOOM);

        // Init summaries
        p_host.setSummary(preferences.getString(PREF_H, null));
        p_port.setSummary(preferences.getString(PREF_POR, null));
        p_place.setSummary(preferences.getString(PREF_DEF_PLACE, null));
        p_zoom.setSummary(preferences.getString(PREF_ZOOM, null));
    }

    public static String getServerHost(){
        return (String) p_host.getSummary();
    }
    public static int getServerPort() { return Integer.parseInt(p_port.getSummary().toString()); }
    public static String getDefaultPlace(){
        return (String) p_place.getSummary();
    }
    public static int getDefaultZoom() { return Integer.parseInt(p_zoom.getSummary().toString()); }

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
