package com.crolopez.smartfridge;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Home extends Fragment {
    private String TAG = "HOME";
    private Button sync_button;
    private Context context;
    private String state_file;
    private TextView text_sync_state;
    private ImageView shield;

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
        text_sync_state = (TextView) myFragmentView.findViewById(R.id.id_sync_state);
        shield = (ImageView) myFragmentView.findViewById(R.id.id_shield);
        context = MainActivity.get_application_context();
        state_file = MainActivity.get_application_cache_dir() + "sync.state";

        shield.setImageResource(R.drawable.shield);

        set_last_sync();

        return myFragmentView;
    }

    private void sync_database() {
        ServerConnect socket_obj;

        set_start_sync();
        socket_obj = new ServerConnect(Setting.getServerHost(), Setting.getServerPort(),  5 * 1000);
        socket_obj.request_db_sync();
        if (socket_obj.get_runnable_result()) {
            update_current_sync();
        } else {
            set_fail_sync();
        }
    }

    private void set_fail_sync() {
        Log.d(TAG, "The database could not be synchronized.");
        ToastMsg.show_toast_msg(context,"Synchronization failed");
    }

    private void set_start_sync() {
        Log.d(TAG, "Starting database synchronization.");
        ToastMsg.show_toast_msg(context,"Starting synchronization");
    }

    private void update_current_sync() {
        Log.d(TAG, "Ending database synchronization.");
        save_sync_time();
        set_last_sync();
        ToastMsg.show_toast_msg(context,"Synchronization successful");
    }

    private void save_sync_time() {
        PrintWriter file_output;
        String buffer;
        Calendar date;

        try {
            file_output = new PrintWriter(state_file);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Exception: save_sync_time(): 1");
            e.printStackTrace();
            return;
        }

        date= Calendar.getInstance();
        buffer = date.get(Calendar.DAY_OF_MONTH) + "/" +
                date.get(Calendar.MONTH) + "/" +
                date.get(Calendar.YEAR) + " " +
                date.get(Calendar.HOUR_OF_DAY) + ":" +
                date.get(Calendar.MINUTE) + ":" +
                date.get(Calendar.SECOND);
        file_output.println(buffer);
        file_output.close();
    }

    private void set_last_sync() {
        BufferedReader file_input;
        String buffer;

        if (!new File(state_file).exists()) {
            return;
        }

        try {
            file_input = new BufferedReader(new FileReader(state_file));
        } catch (FileNotFoundException e) {
            Log.d(TAG, "Exception: set_last_sync(): 1");
            e.printStackTrace();
            return;
        }

        try {
            buffer = file_input.readLine();
        } catch (IOException e) {
            Log.d(TAG, "Exception: set_last_sync(): 2");
            e.printStackTrace();
            return;
        }

        text_sync_state.setTextColor(Color.parseColor(context.getResources().getString(0 + R.color.colorApproves)));
        text_sync_state.setText("Last sync: " + buffer);
    }
}
