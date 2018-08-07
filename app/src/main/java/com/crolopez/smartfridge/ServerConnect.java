package com.crolopez.smartfridge;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

enum operations {
    DB_SYNC,
    UNKNOWN
};

public class ServerConnect extends Application implements Runnable {
    private String TAG = "SERVER_CONNECT";
    private String s_host;
    private int s_port;
    private int s_timeout_ms = 0;
    private operations op_type = operations.UNKNOWN;
    private String server_response = null;
    private String db_name = "products.db";
    private Context context = null;

    public ServerConnect(String host, int port, int timeout) {
        s_host = host;
        s_port = port;
        s_timeout_ms = timeout;

        if (context == null) {
            context = MainActivity.get_application_context();
        }
        super.onCreate();
    }

    @Override
    public void run() {
        Socket socket = get_socket();

        switch(op_type) {
            case DB_SYNC:
                send_request(socket, "!#2+");
                try {
                    receive_database(socket);
                } catch (IOException e) {
                    Log.d(TAG, "Exception: run()-DB_SYNC: 1");
                    e.printStackTrace();
                }
                break;
            default:

                break;
        }

        op_type = operations.UNKNOWN;
        try {
            socket.close();
        } catch (IOException e) {
            Log.d(TAG, "Exception: run(): 0");
            e.printStackTrace();
        }
    }

    public String request_db_sync() {
        Thread thread;
        String str = "Sync error.";
        op_type = operations.DB_SYNC;

        thread = new Thread(this);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.d(TAG, "Exception: request_db_sync(): 1");
            e.printStackTrace();
        }

        return str;
    }

    private Socket get_socket() {
        InetAddress server_addr;
        long sync_start_time;
        Socket socket;

        try {
            server_addr = InetAddress.getByName(s_host);
            sync_start_time = System.currentTimeMillis();
            socket = new Socket();
            socket.connect(new InetSocketAddress(server_addr, s_port), s_timeout_ms);
            Log.d(TAG, "Connection established with the server: " + (System.currentTimeMillis() - sync_start_time) + "ms");
        } catch (UnknownHostException e) {
            Log.d(TAG, "Exception: run(): 1");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.d(TAG, "Exception: run(): 2");
            e.printStackTrace();
            return null;
        }
        return socket;
    }

    private void send_request(Socket socket, String msg) {
        PrintWriter server_out;

        try {
            server_out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            Log.d(TAG, "Exception: send_request(): 0");
            e.printStackTrace();
            return;
        }

        server_out.println(msg);
    }

    private void receive_database(Socket socket) throws IOException {
        InputStream server_in;
        byte[] buffer = new byte[1024];
        int readed;
        File cache_file;
        FileOutputStream file_output;
        int header = 0; // Not used yet (TCP)

        try {
            server_in = socket.getInputStream();
        } catch (IOException e) {
            Log.d(TAG, "Exception: receive_database(): 1");
            e.printStackTrace();
            return;
        }


        cache_file = new File(context.getCacheDir(), db_name);
        file_output = new FileOutputStream(cache_file);

        // Get header
        if ((readed = server_in.read(buffer)) != -1) {
            int msg_position = (new String(buffer)).indexOf("!");
            Log.d(TAG, "Readed: "+ readed + " MSG_position: " + msg_position);
            Log.d(TAG, "Header: '" + new String(buffer, 0, msg_position) + "'");

            // Check if the header has part of the file
            if (msg_position + 1 < readed) {
                file_output.write(buffer, msg_position + 1, readed - (msg_position + 1));
            }
            // Get response
            while((readed = server_in.read(buffer)) > 0) {
                file_output.write(buffer, 0, readed);
            }
        }

        file_output.close();
        Log.d(TAG, "Database received.");
    }
}
