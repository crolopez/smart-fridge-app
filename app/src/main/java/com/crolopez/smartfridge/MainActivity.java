package com.crolopez.smartfridge;


import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.crolopez.smartfridge.R.layout.activity_main);

        getFragmentManager().beginTransaction().add(com.crolopez.smartfridge.R.id.myfragment, new Home()).commit();

        bottomNavigationView = (BottomNavigationView) findViewById(com.crolopez.smartfridge.R.id.id_BottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment newFragment = null;

                switch(item.getItemId()){
                    case com.crolopez.smartfridge.R.id.icon_home:
                        newFragment = new Home();
                        break;
                    case com.crolopez.smartfridge.R.id.icon_search:
                        newFragment = new Search();
                        break;
                    case com.crolopez.smartfridge.R.id.icon_list:
                        newFragment = new List();
                        break;
                    case com.crolopez.smartfridge.R.id.icon_setting:
                        newFragment = new Setting();
                        break;
                    case com.crolopez.smartfridge.R.id.icon_map:
                        newFragment = new Map();
                        break;
                }

                getFragmentManager().beginTransaction().replace(com.crolopez.smartfridge.R.id.myfragment, newFragment).commit();

                return true;
            }
        });
    }


}
