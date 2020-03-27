package a2.mobile.mobileapp.fragments;

import android.os.Bundle;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import a2.mobile.mobileapp.R;

public class MainActivityFragment extends Fragment {

    private static Scene routesScene;
    private static Scene routeDetailsScene;
    private ViewGroup rootScene;

    public static MainActivityFragment newInstance() {
        return new MainActivityFragment();
    }

    public static void switchScene(int id) {
        switch (id) {
            case R.layout.routes_scene:
                TransitionManager.go(routesScene);

                break;
            case R.layout.route_deails_scene:
                TransitionManager.go(routeDetailsScene);

                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_activity_content_fragment, container, false);

        assert view != null;
        rootScene = view.findViewById(R.id.scene_manager);
        routesScene = Scene.getSceneForLayout(
                rootScene,
                R.layout.routes_scene,
                getActivity()
        );

        routeDetailsScene = Scene.getSceneForLayout(
                rootScene,
                R.layout.route_deails_scene,
                getActivity()
        );

        return view;
    }
}
