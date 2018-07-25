package com.crolopez.smartfridge;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import static android.content.ContentValues.TAG;

public class ServerConnect implements Runnable {
    byte[] data;
    private String s_host;
    private int s_port;
    private boolean hasMessage = false;
    int dataType = 1;
    private ByteArrayOutputStream request = null;
    private byte[] recv_msg = null;

    public ServerConnect(String host, int port) {
        s_host = host;
        s_port = port;
    }

    @Override
    public void run() {
        Socket socket = null;
        int bytesRead;
        InputStream input_stream;
        InetAddress server_addr;
        long sync_start_time = 0l;

        if (request == null) {
            request = new ByteArrayOutputStream(1024);
        }

        if (recv_msg == null) {
            recv_msg = new byte[1024];
        }

        try {
            Log.d("myTag", "~~~~~~~~~~~~ run()0 " + s_host);
            server_addr = InetAddress.getByName(s_host);
            Log.d("myTag", "~~~~~~~~~~~~ run()1 " + s_host);
            sync_start_time = System.currentTimeMillis();
            Log.d("myTag", "~~~~~~~~~~~~ run()2 " + s_host);
            socket = new Socket();
            Log.d("myTag", "~~~~~~~~~~~~ run()3 " + s_host);
            socket.connect(new InetSocketAddress(server_addr, s_port), 5000);
            Log.d(TAG, "Connection established with the server: " + (System.currentTimeMillis() - sync_start_time) + "ms");
            //socket.close();
        } catch (UnknownHostException e) {
            Log.e(TAG, "Exception: sync_database(): 0");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Exception: sync_database(): 1");
            e.printStackTrace();
        }
    }
}
