package a2.mobile.mobileapp.common.login;

import android.content.Intent;

public class AuthenticationOption {
    public int id;
    public int title;
    public int icon;
    public Intent activityToStart;

    public AuthenticationOption(int id, int title, int icon, Intent activityToStart) {
        this.id = id;
        this.title = title;
        this.icon = icon;
        this.activityToStart = activityToStart;
    }
}
