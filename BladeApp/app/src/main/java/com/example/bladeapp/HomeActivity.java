package com.example.bladeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class HomeActivity extends Activity {

    private Button toDeviceButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toDeviceButton = findViewById(R.id.button_todevicelist);
    //click the button to jump to next activity
        toDeviceButton.setOnClickListener(new DeviceButtonClick());

    }
    private void deviceButtonClicked() {
        Intent devicePage = new Intent(getApplicationContext(),DeviceList.class);
        startActivity(devicePage);
    }

    class DeviceButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            deviceButtonClicked();
        }
    }

}
