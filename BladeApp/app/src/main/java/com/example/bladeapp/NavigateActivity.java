package com.example.bladeapp;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.LogPrinter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.connectivity.sdk.Connectivity;

import org.w3c.dom.Text;

public class NavigateActivity extends Activity {

    private ImageButton btn_show_popup;
    private PopupWindow mPopupWindow;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private Activity mActivity;
    private TextView navigation_message_view;
    private LinearLayout navigation_message_holder;
    private static final String ACTION_SEND = "a2.mobile.mobileapp.SEND";
    private static final String EXTRA_TEXT = "my_string_extra";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        mContext = getApplicationContext();
        mActivity = NavigateActivity.this;

        navigation_message_view = findViewById(R.id.navigation_text);

        mLinearLayout =(LinearLayout)findViewById(R.id.pop_up_holder);
        navigation_message_holder = (LinearLayout)findViewById(R.id.text_holder);

        btn_show_popup = (ImageButton) findViewById(R.id.button_popup);
        btn_show_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(NavigateActivity.this);
                assert inflater != null;
                View customView = inflater.inflate(R.layout.popup,null);

                mPopupWindow = new PopupWindow(
                        customView,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                // Set an elevation value for popup window
                // Call requires API level 21
                if(Build.VERSION.SDK_INT>=21){
                    mPopupWindow.setElevation(5.0f);
                }
                Button closeButton = (Button) customView.findViewById(R.id.close_pop_up);
                // Set a click listener for the popup window close button
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Dismiss the popup window
                        mPopupWindow.dismiss();
                        navigation_message_holder.setVisibility(View.VISIBLE);
                        btn_show_popup.setVisibility(View.VISIBLE);
                    }
                });
                navigation_message_holder.setVisibility(View.INVISIBLE);
                btn_show_popup.setVisibility(View.GONE);
                mPopupWindow.showAtLocation(new LinearLayout(NavigateActivity.this), Gravity.CENTER,20,20);



            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(ACTION_SEND));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Connectivity.get(context).verify(intent, "a2.mobile.mobileapp")) {
                String text =intent.getStringExtra(EXTRA_TEXT);
                if(text != null){
                    navigation_message_view.setText(text);
                }
            }
        }
    };
}
