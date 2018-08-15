package com.crolopez.smartfridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TrackPosition extends BroadcastReceiver {
    private String TAG = "TRACKER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Entering on onReceive().");
        Map.check_proximity();
    }

}