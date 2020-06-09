package com.example.bladeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PopUpActivity extends Activity {
    private static final String low = "LOW";
    private static final String medium = "MEDIUM";
    private static final String high = "HIGH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup);

        final Intent intent = getIntent();
        String[] message = intent.getStringArrayExtra("Point of Interest");
        LinearLayout mLinearLayout =findViewById(R.id.pop_up_holder);
        assert message != null;
        if(message[2].equals(low)){
            mLinearLayout.setBackgroundResource(R.drawable.border);
        }else if(message[2].equals(medium)){
            mLinearLayout.setBackgroundResource(R.drawable.boarder_orange);
        }else if(message[2].equals(high)){
            mLinearLayout.setBackgroundResource(R.drawable.border_red);
        }
        TextView title = findViewById(R.id.point_Of_interest);
        TextView descriptiption_of_point = findViewById(R.id.details_of_point);
        assert message != null;
        title.setText(message[0]);
        descriptiption_of_point.setText(message[1]);

        //close button
        Button closeButton = (Button) findViewById(R.id.close_pop_up);
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(2,intent);
                finish();
            }
        });


    }
}
