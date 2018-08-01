package com.crolopez.smartfridge;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Home extends Fragment {
    private String TAG = "HOME";
    private Button sync_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myFragmentView = inflater.inflate(R.layout.activity_home, container, false);
        sync_button = (Button) myFragmentView.findViewById(R.id.id_sync_button);
        sync_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sync_database();
            }
        });

        return myFragmentView;
    }

    private void sync_database() {
        ServerConnect socket_obj;

        Log.d(TAG, "Starting database synchronization.");
        socket_obj = new ServerConnect(Setting.getServerHost(), Setting.getServerPort(),  5 * 1000);
        socket_obj.request_db_sync();

        Log.d(TAG, "Ending database synchronization.");
        update_current_sync();
    }

    private void set_fail_sync() {

    }

    private void update_current_sync() {

    }
}
