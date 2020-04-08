package a2.mobile.mobileapp.fragments;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import a2.mobile.mobileapp.R;

public class MainActivityMapFragment extends Fragment {

    private Context context;
    private View rootView;

    private CardView directionCardView;
    private TextView iconTextView;

    public MainActivityMapFragment(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;

        directionCardView = rootView.findViewById(R.id.current_direction);
        iconTextView = directionCardView.findViewById(R.id.icon);
    }

    public void setUpViewForNavigation() {
        directionCardView.setVisibility(View.VISIBLE);

        collapseContentHolder();
    }

    public void collapseContentHolder() {
        // TODO: Collapse content holder and change/rotate the icon.
    }

    public void updateCurrentDirection(int icon) {
        iconTextView.setText(icon);
    }

    public void setUpViewAfterNavigationExit() {
        directionCardView.setVisibility(View.INVISIBLE);
    }
}
