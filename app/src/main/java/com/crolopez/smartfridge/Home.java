package com.crolopez.smartfridge;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;


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
        Socket socket;
        Thread socket_t;
        ServerConnect socket_obj;
        PrintWriter socket_out;

        Log.d(TAG, "Starting database synchronization.");
        socket_obj = new ServerConnect(Setting.getServerHost(), Setting.getServerPort(),  5 * 1000);
        socket_t = new Thread(socket_obj);
        socket_t.start();
        try {
            sleep(socket_obj.get_sock_timeout() + 1000);
        } catch (InterruptedException e) {
            Log.d(TAG, "Exception: sync_database(): 0");
            e.printStackTrace();
        }
        socket = socket_obj.get_socket();
        if (socket == null) {
            Log.d(TAG, "The database could not be synchronized.");
            set_fail_sync();
        } else {
            try {
                socket_out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            } catch (IOException e) {
                Log.d(TAG, "Exception: sync_database(): 1");
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                Log.d(TAG, "Exception: sync_database(): 2");
                e.printStackTrace();
            }



            Log.d(TAG, "Ending database synchronization.");
            update_current_sync();
        }
    }

    private void set_fail_sync() {

    }

    private void update_current_sync() {

    }
}
