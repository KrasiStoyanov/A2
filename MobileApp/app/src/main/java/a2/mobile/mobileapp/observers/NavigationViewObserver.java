package a2.mobile.mobileapp.observers;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.mapbox.services.android.navigation.ui.v5.NavigationView;

import javax.security.auth.callback.Callback;

import a2.mobile.mobileapp.R;

import static androidx.lifecycle.Lifecycle.State.STARTED;

public class NavigationViewObserver implements LifecycleObserver {
    private boolean enabled = false;

    private Context context;
    private Lifecycle lifecycle;
    private Callback callback;

    private View rootView;
    private NavigationView navigationView;

    public NavigationViewObserver(Context context, Lifecycle lifecycle, Callback callback) {
        this.context = context;
        this.lifecycle = lifecycle;
        this.callback = callback;

        this.rootView = ((Activity)context).findViewById(R.id.activity_main);
        this.navigationView = this.rootView.findViewById(R.id.mapbox_navigation);
        Log.e("View", "Navigation Viw " + navigationView);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void start() {
        if (enabled) {
            // connect
        }
    }

    public void enable() {
        enabled = true;
        if (lifecycle.getCurrentState().isAtLeast(STARTED)) {
            // connect if not connected
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void stop() {
        // disconnect if connected
    }
}
