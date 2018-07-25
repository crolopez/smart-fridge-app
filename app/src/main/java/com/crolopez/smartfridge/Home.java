package com.crolopez.smartfridge;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


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
        Log.d("myTag", "~~~~~~~~~~~~ sync_database()0");
        new Thread(new ServerConnect(Setting.getServerHost(), Setting.getServerPort())).start();
        Log.d("myTag", "~~~~~~~~~~~~ sync_database()1");
    }
}
