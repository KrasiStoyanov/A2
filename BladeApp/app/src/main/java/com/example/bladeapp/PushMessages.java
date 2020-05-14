package com.example.bladeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PushMessages extends Activity {
    private Button toNavigationPage;
    private Button btnDecline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_message);
        //call acceptButton
        toNavigationPage = (Button)findViewById(R.id.button_accept);
        toNavigationPage.setOnClickListener(new acceptButtonClick());
        //call declineButton
        btnDecline = (Button)findViewById(R.id.button_decline);
        btnDecline.setOnClickListener(new declineButtonClick());
    }

    private void acceptButtonClicked(){
        Intent tonavigation = new Intent(getApplicationContext(),NavigateActivity.class);
        startActivity(tonavigation);
    }
    class acceptButtonClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            acceptButtonClicked();
        }
    }

    class declineButtonClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
