package com.example.bladeapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;


import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.vuzix.connectivity.sdk.Connectivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Navigationpage extends Activity  {
    private PopupWindow mPopupWindow;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private Activity mActivity;
    public List<ImageButton> notification_list = new ArrayList<>();
    private int notification_index;
    //public Runnable mRunnerble;
    private static final String low = "LOW";
    private static final String medium = "MEDIUM";
    private static final String high = "HIGH";
    private TextView navigation_message_view;
    private TextView navigation_distance_view;
    public TextView details_of_points;
    public TextView title_point_of_interst;
    private LinearLayout navigation_message_holder;
    private ImageView directionIcon;
    private CardView cardView;
    private static final String ACTION_SEND = "a2.mobile.mobileapp.SEND";
    //Test
    private ImageButton notification_icon;
    private LinearLayout notification_icon_holder;

    //BoradcastReceiver
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
    //Generate a BroadcastReceiver to receive the data from Mobile application
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //check if the app connect with the correct mobile application(package)
            if (Connectivity.get(context).verify(intent, "a2.mobile.mobileapp")) {
                //Receive distance of navigation and set it on the layout
                String[] distance = intent.getStringArrayExtra("distance_remaining");
                if (distance != null) {
                    navigation_distance_view.setText(distance[0]);
                }
                //Receive navigation text and set it on the layout
                String[] navigation_text = intent.getStringArrayExtra("icon_name");
                if (navigation_text != null) {
                    Log.e("Text",navigation_text[0]);
                    navigation_message_view.setText(navigation_text[0]);

                    cardView.setVisibility(View.VISIBLE);
                    if(navigation_text[0].contains("left")){
                        directionIcon.setImageResource(R.drawable.ic_arrow_back_black_18dp);
                    }else if(navigation_text[0].contains("right")){
                        directionIcon.setImageResource(R.drawable.ic_arrow_forward_black_18dp);
                    }else if(navigation_text[0].contains("straight")){
                        directionIcon.setImageResource(R.drawable.ic_arrow_upward_24px);
                    }

                }
                //Receive point of interest
                final String[] title = intent.getStringArrayExtra("interest point title");

                if (title != null) {
                    Log.e("Title", Arrays.toString(title));

                    //Set notification icon based on the priority of the point of interest
                    notification_icon = new ImageButton(mContext);notification_icon.setTag("button_popup");
                    notification_icon.setLayoutParams(new LinearLayout.LayoutParams(85,95));
                    notification_icon.setBackgroundColor(getResources().getColor(R.color.transparent_Black));
                    if(title[2].equals(low)){
                        notification_icon.setImageResource(R.drawable.ic_notifications_blue_18dp);
                    }else if(title[2].equals(medium)){
                        notification_icon.setImageResource(R.drawable.ic_notification_orange);
                    }else if(title[2].equals(high)){
                        notification_icon.setImageResource(R.drawable.ic_notification_red);
                    }
                    notification_icon_holder.addView(notification_icon);

                    notification_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            notification_index = notification_list.indexOf(notification_icon);
                            Intent intent = new Intent(mActivity, PopUpActivity.class);
                            String[] message = title;
                            intent.putExtra("Point of Interest", message);
                            startActivityForResult(intent,2);

                        }
                    });
                    //Time out function for the notifications.
                    notification_list.add(notification_icon);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Add the line which you want to run after 5 sec.
                            Toast.makeText(mContext,"The notification will dispear after 10s",Toast.LENGTH_SHORT).show();
                        }
                    },5000);
                    Handler mHandler = new Handler();
                     Runnable mRunnerble = new Runnable() {
                        @Override
                        public void run() {
                            if(notification_index>= 0 && notification_index<notification_list.size()){
                                ImageButton current_notification_icon = notification_list.remove(notification_index);
                                current_notification_icon.setVisibility(View.GONE);
                            }
                        }

                    };
                    mHandler.postDelayed(mRunnerble,15*1000);
                }

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        mContext = getApplicationContext();
        mActivity = Navigationpage.this;
    //initialize  layouts
        navigation_message_view = findViewById(R.id.navigation_text);
        navigation_distance_view = findViewById(R.id.distance_holder);
        navigation_message_holder = (LinearLayout)findViewById(R.id.text_holder);
        
        notification_icon_holder = findViewById(R.id.notification_icon_holder);
        cardView = findViewById(R.id.imageView);
        directionIcon = findViewById(R.id.direction_icon);

    }
    //Disappear the notification icons when the user close the pop-up window.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 2){
                if(notification_index>= 0 && notification_index<notification_list.size()) {
                    ImageButton current_notification_icon = notification_list.remove(notification_index);
                    current_notification_icon.setVisibility(View.GONE);
                }
            }
    }

}
