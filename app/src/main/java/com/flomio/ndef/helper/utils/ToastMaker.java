package com.flomio.ndef.helper.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ti.nfcdemo.R;

/**
 * Created by Udayan on 5/21/13.
 */
public class ToastMaker {

    public static final int STYLE_SUCCESS = 100;
    public static final int STYLE_FAILURE = 200;
    public static final int STYLE_INFO = 300;


    public static void makeToastLong(Context context, String text, int style) {

        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toastview, null);
        TextView textView = (TextView) layout.findViewById(R.id.toast_view_text);
        textView.setText(text);

        switch (style) {
            case STYLE_FAILURE:
                textView.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case STYLE_SUCCESS:
                textView.setBackgroundColor(context.getResources().getColor(R.color.lime));
                break;
            case STYLE_INFO:
                textView.setBackgroundColor(context.getResources().getColor(R.color.teal));
                break;
            default:
                textView.setBackgroundColor(context.getResources().getColor(R.color.teal));
                break;

        }

        toast.setView(layout);
        toast.show();
    }

    public static void makeToastShort(Context context, String text, int style) {


        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toastview, null);
        TextView textView = (TextView) layout.findViewById(R.id.toast_view_text);
        textView.setText(text);

        switch (style) {
            case STYLE_FAILURE:
                textView.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case STYLE_SUCCESS:
                textView.setBackgroundColor(context.getResources().getColor(R.color.lime));
                break;
            case STYLE_INFO:
                textView.setBackgroundColor(context.getResources().getColor(R.color.teal));
                break;
            default:
                textView.setBackgroundColor(context.getResources().getColor(R.color.teal));
                break;
        }

        toast.setView(layout);
        toast.show();

    }
}
