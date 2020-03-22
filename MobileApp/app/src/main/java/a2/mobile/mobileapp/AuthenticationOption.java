package a2.mobile.mobileapp;

import android.content.Intent;

public class AuthenticationOption {
    public int title;
    public int icon;
    public Intent activityToStart;

    AuthenticationOption(int title, int icon, Intent activityToStart) {
        this.title = title;
        this.icon = icon;
        this.activityToStart = activityToStart;
    }
}
