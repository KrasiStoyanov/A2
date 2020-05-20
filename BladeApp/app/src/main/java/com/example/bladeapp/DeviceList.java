package com.example.bladeapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vuzix.connectivity.sdk.Connectivity;
import com.vuzix.connectivity.sdk.Device;

public class DeviceList extends Activity {
    private static final String ACTION_GET = "com.example.bladeapp.GET";
    private ImageButton Topushbutton;
    private Context mContext;
    private static final String EXTRA_TEXT = "text";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list);

        mContext = getApplicationContext();
        Topushbutton = (ImageButton)findViewById(R.id.button_toPush_messages);
        Topushbutton.setOnClickListener(new TopushButtonClick());
        //check for Connectivity framework
        if (!Connectivity.get(this).isAvailable()) {
            Toast.makeText(mContext, R.string.not_available, Toast.LENGTH_SHORT).show();
            finish();
            return;

        }else {
            getRemoteDevice();
        }
    }
    private void topushbuttonclicked(){
        Intent pushpage = new Intent(getApplicationContext(),PushMessages.class);
        startActivity(pushpage);
    }
    class TopushButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            topushbuttonclicked();
        }
    }



    public void getRemoteDevice() {
        Connectivity connectivity = Connectivity.get(mContext);
        final Device device = connectivity.getDevice();
         final TextView textView = (TextView) findViewById(R.id.devicename);

            if (device != null) {
                Intent getIntent = new Intent(ACTION_GET);
                getIntent.setPackage("com.example.bladeapp");
                String devicename = device.getName();
                if(devicename != null){
                    textView.setText(devicename);
                }

            }
    }


}
