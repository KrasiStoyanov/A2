package com.example.bladeapp;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class HomeActivity extends Activity {
    ListView listView;
    public View view;
    private Button toDeviceButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toDeviceButton = findViewById(R.id.button_todevicelist);

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
