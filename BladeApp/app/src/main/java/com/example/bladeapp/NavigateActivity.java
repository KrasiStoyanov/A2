package com.example.bladeapp;


import android.app.Activity;
import android.content.Context;
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

public class NavigateActivity extends Activity {
    private Point p;
    private ImageButton btn_show_popup;
    private Button close;
    private PopupWindow mPopupWindow;
    private LinearLayout mLinearLayout;
    private Context mContext;
    private Activity mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        mContext = getApplicationContext();
        mActivity = NavigateActivity.this;

        mLinearLayout =(LinearLayout)findViewById(R.id.pop_up_holder);
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
                    }
                });
                Log.e("test Layou","?"+mLinearLayout);
                mPopupWindow.showAtLocation(new LinearLayout(NavigateActivity.this), Gravity.CENTER,0,0);


            }
        });
    }
    /*@Override
    public void onWindowFocusChanged(boolean hasfocus){
        int[] location = new int[2];
        ImageButton imageButton = (ImageButton) findViewById(R.id.button_popup);
        imageButton.getLocationOnScreen(location);
        p = new Point();
        p.x = location[0];
        p.y = location[1];
    }

    private void showPopup(final Activity context, Point p) {
        int popupWidth = 200;
        int popupHeight = 150;
        // Inflate the popup.xml
        setContentView(R.layout.popup);
        LinearLayout viewGroup = (LinearLayout)findViewById(R.id.pop_up_holder);
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View layout = layoutInflater.inflate(R.layout.popup, viewGroup);
        // Creating the PopupWindow
        final PopupWindow popup = new PopupWindow(context);
        popup.setContentView(layout);
        popup.setWidth(popupWidth);
        popup.setHeight(popupHeight);
        popup.setFocusable(true);
        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        int OFFSET_X = 30;
        int OFFSET_Y = 30;
        // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable(context.getResources()));

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);
        close = (Button)findViewById(R.id.close_pop_up);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        }) ;
    }
*/
}
