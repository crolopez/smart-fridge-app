package com.crolopez.smartfridge;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {
    private static String TAG = "MAIN";
    private BottomNavigationView bottomNavigationView;
    private Home home_obj = null;
    private Inventory search_obj = null;
    private ShoppingList list_obj = null;
    private Setting setting_obj = null;
    private Map map_obj = null;
    private static Context application_context = null;
    private static String application_cache_dir = null;
    private static Activity activity = null;
    private static final int REQUEST_READ_FINE_LOCATION = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (activity == null) {
            activity = this;
        }

        if (home_obj == null) {
            home_obj = new Home();
        }

        if (setting_obj == null) {
            setting_obj = new Setting();
        }

        if (application_context == null) {
            application_context = getApplicationContext();
        }

        if (application_cache_dir == null) {
            application_cache_dir = application_context.getCacheDir().getAbsolutePath() + "/";
        }

        setContentView(com.crolopez.smartfridge.R.layout.activity_main);
        getFragmentManager().beginTransaction().add(com.crolopez.smartfridge.R.id.myfragment, setting_obj).commit();
        getFragmentManager().beginTransaction().replace(com.crolopez.smartfridge.R.id.myfragment, home_obj).commit();

        bottomNavigationView = (BottomNavigationView) findViewById(com.crolopez.smartfridge.R.id.id_BottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment newFragment = null;

                switch(item.getItemId()){
                    case com.crolopez.smartfridge.R.id.icon_home:
                        if (home_obj == null) {
                            home_obj = new Home();
                        }
                        newFragment = home_obj;
                        break;
                    case R.id.icon_inventory:
                        if (search_obj == null) {
                            search_obj = new Inventory();
                        }
                        newFragment = search_obj;
                        break;
                    case com.crolopez.smartfridge.R.id.icon_list:
                        if (list_obj == null) {
                            list_obj = new ShoppingList();
                        }
                        newFragment = list_obj;
                        break;
                    case com.crolopez.smartfridge.R.id.icon_setting:
                        if (setting_obj == null) {
                            setting_obj = new Setting();
                        }
                        newFragment = setting_obj;
                        break;
                    case com.crolopez.smartfridge.R.id.icon_map:
                        ToastMsg.show_toast_msg(application_context,"Loading map..");
                        if (map_obj == null) {
                            map_obj = new Map();
                        }
                        newFragment = map_obj;
                        break;
                }

                getFragmentManager().beginTransaction().replace(com.crolopez.smartfridge.R.id.myfragment, newFragment).commit();

                return true;
            }
        });
    }

    static Context get_application_context() {
        return application_context;
    }
    static String get_application_cache_dir() {
        return application_cache_dir;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_READ_FINE_LOCATION && grantResults[0] < 0) {
            Setting.setGeolocation();
            ToastMsg.show_toast_msg(MainActivity.get_application_context(),
                    "You need to scale permissions to activate geolocation");
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void ask_permissions() {
        Log.d(TAG, "Entering on ask_permissions().");
        ActivityCompat.requestPermissions(
                activity,
                new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                REQUEST_READ_FINE_LOCATION
        );
    }

}
