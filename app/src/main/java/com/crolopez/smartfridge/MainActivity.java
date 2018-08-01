package com.crolopez.smartfridge;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends FragmentActivity {

    private BottomNavigationView bottomNavigationView;
    private Home home_obj = null;
    private Search search_obj = null;
    private List list_obj = null;
    private Setting setting_obj = null;
    private Map map_obj = null;
    private static Context application_context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (home_obj == null) {
            home_obj = new Home();
        }

        if (setting_obj == null) {
            setting_obj = new Setting();
        }

        if (application_context == null) {
            application_context = getApplicationContext();
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
                    case com.crolopez.smartfridge.R.id.icon_search:
                        if (search_obj == null) {
                            search_obj = new Search();
                        }
                        newFragment = search_obj;
                        break;
                    case com.crolopez.smartfridge.R.id.icon_list:
                        if (list_obj == null) {
                            list_obj = new List();
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

}
