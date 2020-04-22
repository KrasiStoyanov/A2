package a2.mobile.mobileapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.handlers.MapHandler;
import a2.mobile.mobileapp.handlers.NavigationHandler;
import a2.mobile.mobileapp.utils.NavigationUtils;

public class MainActivityMapFragment extends Fragment {

    private Context context;
    private View rootView;

    private Scene mapScene;
    private Scene navigationScene;

    private int currentScene;

    public MainActivityMapFragment(Context context) {
        this.context = context;
    }

    /**
     * Switch scenes based on the provided scene ID.
     *
     * @param id The scene to play
     */
    void switchScene(int id) {
        if (currentScene == id) {
            return;
        }

        switch (id) {
            case R.layout.scene_map_view:
                TransitionManager.go(mapScene);
                break;
            case R.layout.scene_navigation_view:
                TransitionManager.go(navigationScene);
                NavigationUtils.startNavigation(context);
                break;
        }

        currentScene = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.main_activity_map_fragment,
                container,
                false
        );

        assert view != null;
        rootView = view.findViewById(R.id.map_scene_manager);
        mapScene = Scene.getSceneForLayout(
                (ViewGroup) rootView,
                R.layout.scene_map_view,
                getActivity()
        );

        navigationScene = Scene.getSceneForLayout(
                (ViewGroup) rootView,
                R.layout.scene_navigation_view,
                getActivity()
        );

        NavigationUtils.storeRootView(rootView);

        return view;
    }
}
