package com.jcimadras.jcimadras.Extras;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jcimadras.jcimadras.R;


public class Message {
    public static void ts(Context context, String message, LayoutInflater li, View vg) {
        View layout = li.inflate(R.layout.toast, (ViewGroup) vg);
        TextView textView = (TextView) layout.findViewById(R.id.toast);
        textView.setText(message);
        Toast t = new Toast(context);
        t.setDuration(Toast.LENGTH_SHORT);
//        t.setGravity(Gravity.CENTER, 0, 0);
        t.setView(layout);
        t.show();
    }

    public static void tl(Context context, String message, LayoutInflater li, View vg) {
        View layout = li.inflate(R.layout.toast, (ViewGroup) vg);
        TextView textView = (TextView) layout.findViewById(R.id.toast);
        textView.setText(message);
        Toast t = new Toast(context);
        t.setDuration(Toast.LENGTH_LONG);
//        t.setGravity(Gravity.CENTER, 0, 0);
        t.setView(layout);
        t.show();
    }

    public static void L(String Tag, String message) {
        Log.e(Tag, message);
    }
}
