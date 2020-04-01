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
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.handlers.RouteDetailsHandler;

public class MainActivityFragment extends Fragment {

    private Context context;
    public View rootView;

    private Scene routesScene;
    private Scene routeDetailsScene;

    private static int currentScene;

    public MainActivityFragment(Context context) {
        this.context = context;
    }

    /**
     * Switch scenes based on the provided scene ID.
     *
     * @param id The scene to play
     */
    public void switchScene(int id) {
        if (currentScene == id) {
            return;
        }

        switch (id) {
            case R.layout.routes_scene:
                TransitionManager.go(routesScene);
                RoutesHandler.handleRoutes(
                        context,
                        rootView,
                        Data.routes
                );

                currentScene = R.layout.routes_scene;
                break;
            case R.layout.route_deails_scene:
                TransitionManager.go(routeDetailsScene);
                RouteDetailsHandler.handleRouteSelection(context, rootView);

                currentScene = R.layout.route_deails_scene;
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.main_activity_content_fragment,
                container,
                false
        );

        assert view != null;
        rootView = view.findViewById(R.id.scene_manager);
        routesScene = Scene.getSceneForLayout(
                (ViewGroup) rootView,
                R.layout.routes_scene,
                getActivity()
        );

        routeDetailsScene = Scene.getSceneForLayout(
                (ViewGroup) rootView,
                R.layout.route_deails_scene,
                getActivity()
        );

        return view;
    }
}
