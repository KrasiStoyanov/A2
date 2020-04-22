package a2.mobile.mobileapp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class NavigationViewPartial extends LinearLayout {
    private Context context;

    public NavigationViewPartial(Context rootContext, AttributeSet attributeSet) {
        super(rootContext, attributeSet);

        context = rootContext;
    }
}
