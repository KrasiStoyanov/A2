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
import android.view.LayoutInflater;
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

import java.util.Arrays;

public class Navigationpage extends Activity {
    private PopupWindow mPopupWindow;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private Activity mActivity;
    public Runnable mRunnerble;
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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Connectivity.get(context).verify(intent, "a2.mobile.mobileapp")) {

                String[] distance = intent.getStringArrayExtra("distance_remaining");
                if (distance != null) {
                    navigation_distance_view.setText(distance[0]);
                }
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
                    /*cardView.setVisibility(View.VISIBLE);
                    cardView.setRadius(20);
                    cardView.setContentPadding(13,13,13,13);
                    cardView.getResources().getColor(R.color.button_primary__background);
                    if (navigation_text.equals("left")) {
                        directionIcon.setImageResource(R.drawable.ic_arrow_back_black_18dp);
                    }else if(navigation_text.equals("right")){
                        directionIcon.setImageResource(R.drawable.ic_arrow_forward_black_18dp);
                    }
                }else{
                    cardView.setVisibility(View.INVISIBLE);
                }
                */
                }
                final String[] title = intent.getStringArrayExtra("interest point title");

                if (title != null) {
                    Log.e("Title", Arrays.toString(title));

                    notification_icon = new ImageButton(mContext);notification_icon.setTag("button_popup");
                    notification_icon.setLayoutParams(new LinearLayout.LayoutParams(75,85));
                    notification_icon.setBackgroundColor(getResources().getColor(R.color.transparent_Black));
                    if(title[2].equals(low)){
                        notification_icon.setImageResource(R.drawable.ic_notifications_blue_18dp);
                    }else if(title[2].equals(medium)){
                        notification_icon.setImageResource(R.drawable.ic_notification_orange);
                    }else if(title[2].equals(high)){
                        notification_icon.setImageResource(R.drawable.ic_notification_red);
                    }
                    notification_icon_holder.addView(notification_icon);
                    LayoutInflater inflater = LayoutInflater.from(Navigationpage.this);
                    assert inflater != null;
                    final View customView = inflater.inflate(R.layout.popup,null);
                    notification_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
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
                            mLinearLayout =(LinearLayout)customView.findViewById(R.id.pop_up_holder);
                            if(title[2].equals(low)){
                                mLinearLayout.setBackgroundResource(R.drawable.border);
                            }else if(title[2].equals(medium)){
                                mLinearLayout.setBackgroundResource(R.drawable.boarder_orange);
                            }else if(title[2].equals(high)){
                                mLinearLayout.setBackgroundResource(R.drawable.border_red);
                            }
                            title_point_of_interst = customView.findViewById(R.id.point_Of_interest);
                            details_of_points = customView.findViewById(R.id.details_of_point);
                            title_point_of_interst.setText(title[0]);
                            details_of_points.setText(title[1]);

                            //close button
                            Button closeButton = (Button) customView.findViewById(R.id.close_pop_up);
                            // Set a click listener for the popup window close button
                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // Dismiss the popup window
                                    mPopupWindow.dismiss();
                                    navigation_message_holder.setVisibility(View.VISIBLE);
                                    //notification_icon.setVisibility(View.VISIBLE);

                                }
                            });
                            navigation_message_holder.setVisibility(View.INVISIBLE);
                            notification_icon.setVisibility(View.GONE);
                            mPopupWindow.showAtLocation(new LinearLayout(Navigationpage.this), Gravity.CENTER,20,20);
                        }
                    });
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Add the line which you want to run after 5 sec.
                            Toast.makeText(mContext,"The notification will dispear after 10s",Toast.LENGTH_SHORT).show();
                        }
                    },5000);
                    Handler mHandler = new Handler();
                    mRunnerble = new Runnable() {
                        @Override
                        public void run() {
                            notification_icon.setVisibility(View.GONE);
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

        navigation_message_view = findViewById(R.id.navigation_text);
        navigation_distance_view = findViewById(R.id.distance_holder);
        navigation_message_holder = (LinearLayout)findViewById(R.id.text_holder);
        //cardView =findViewById(R.id.imageView);
        //directionIcon = findViewById(R.id.direction_icon);
        notification_icon_holder = findViewById(R.id.notification_icon_holder);
        cardView = findViewById(R.id.imageView);
        directionIcon = findViewById(R.id.direction_icon);

    }
    /*
    public void creatNotificationIcon(){

            notification_icon = new ImageButton(mContext);
            notification_icon.setTag("button_popup");
            notification_icon.setLayoutParams(new LinearLayout.LayoutParams(69,80));
            notification_icon.setBackgroundColor(getResources().getColor(R.color.transparent_Black));
            notification_icon.setImageResource(R.drawable.ic_notifications_blue_18dp);
            notification_icon_holder.addView(notification_icon);
            LayoutInflater inflater = LayoutInflater.from(Navigationpage.this);
            assert inflater != null;
            final View customView = inflater.inflate(R.layout.popup,null);
            notification_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    mLinearLayout =(LinearLayout)customView.findViewById(R.id.pop_up_holder);
                    title_point_of_interst = customView.findViewById(R.id.point_Of_interest);
                    details_of_points = customView.findViewById(R.id.details_of_point);
                    title_point_of_interst.setText("HHHHH");
                    details_of_points.setText("Love it");
                    //close button
                    Button closeButton = (Button) customView.findViewById(R.id.close_pop_up);
                    // Set a click listener for the popup window close button
                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Dismiss the popup window
                            mPopupWindow.dismiss();
                            navigation_message_holder.setVisibility(View.VISIBLE);
                            //notification_icon.setVisibility(View.VISIBLE);

                        }
                    });
                    navigation_message_holder.setVisibility(View.INVISIBLE);
                    notification_icon.setVisibility(View.GONE);
                    mPopupWindow.showAtLocation(new LinearLayout(Navigationpage.this), Gravity.CENTER,20,20);
                }
            });

            /*if(priority.equals(low)){
                notification_icon.setImageResource(R.drawable.ic_notifications_blue_18dp);
                notification_icon_holder.addView();
            }


        }
*/
}
