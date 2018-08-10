package com.crolopez.smartfridge;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class ToastMsg {
    static int color = -1;

    public static void show_toast_msg(Context context, String msg) {
        Toast toast_msg;
        TextView text;
        if (color == -1) {
            color = Color.parseColor(context.getResources().getString(0 + R.color.colorPrimaryDark));
        }

        toast_msg = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast_msg.setGravity(Gravity.CENTER, 0, 0);
        text = (TextView) toast_msg.getView().findViewById(android.R.id.message);
        text.setTextColor(color);
        text.setTypeface(null, Typeface.BOLD);
        toast_msg.show();
    }
}
