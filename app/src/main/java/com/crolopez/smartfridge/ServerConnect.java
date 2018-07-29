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
    private String TAG = "SERVER_CONNECT";
    byte[] data;
    private String s_host;
    private int s_port;
    private boolean hasMessage = false;
    int dataType = 1;
    private ByteArrayOutputStream request = null;
    private byte[] recv_msg = null;
    private Socket s_socket = null;
    private int s_timeout_ms = 0;

    public ServerConnect(String host, int port, int timeout) {
        s_host = host;
        s_port = port;
        s_timeout_ms = timeout;
    }

    @Override
    public void run() {
        int bytesRead;
        InputStream input_stream;
        InetAddress server_addr;
        long sync_start_time = 0l;
        Socket socket;

        if(s_socket != null) {
            try {
                s_socket.close();
            } catch (IOException e) {
                Log.d(TAG, "Exception: run(): 0");
                e.printStackTrace();
            }
        }

        s_socket = null;

        if (request == null) {
            request = new ByteArrayOutputStream(1024);
        }

        if (recv_msg == null) {
            recv_msg = new byte[1024];
        }

        try {
            server_addr = InetAddress.getByName(s_host);
            sync_start_time = System.currentTimeMillis();
            socket = new Socket();
            socket.connect(new InetSocketAddress(server_addr, s_port), s_timeout_ms);
            Log.d(TAG, "Connection established with the server: " + (System.currentTimeMillis() - sync_start_time) + "ms");
        } catch (UnknownHostException e) {
            Log.d(TAG, "Exception: run(): 1");
            socket = null;
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Exception: run(): 2");
            socket = null;
            e.printStackTrace();
        }

        s_socket = socket;
    }

    public Socket get_socket() {
        return s_socket;
    }

    public int get_sock_timeout() {
        return s_timeout_ms;
    }
}
