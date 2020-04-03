package a2.mobile.mobileapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.function.Function;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.activities.MainActivity;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.handlers.MapHandler;
import a2.mobile.mobileapp.handlers.NavigationHandler;
import a2.mobile.mobileapp.handlers.PointsOfInterestHandler;
import a2.mobile.mobileapp.handlers.RouteDetailsHandler;
import a2.mobile.mobileapp.handlers.RoutesHandler;

public class MainActivityFragment extends Fragment {

    private Context context;
    private View rootView;

    private Scene routesScene;
    private Scene routeDetailsScene;
    private Scene pointsOfInterestScene;
    private Scene navigationScene;

    private int currentScene;

    public static Function onRouteRenderComplete = null;

    public MainActivityFragment(Context context) {
        this.context = context;
    }

    public void goBack() {
        switch (currentScene) {
            case R.layout.scene_route_deails:
                switchScene(R.layout.scene_routes);

                // TODO: Only clear route rendering if there is no active navigation.
                MainActivity.map.clear();

                break;
            case R.layout.scene_points_of_interest:
                switchScene(R.layout.scene_route_deails);

                break;
            case R.layout.scene_navigation:
                NavigationHandler.stopNavigation();

                break;
        }
    }

    /**
     * Switch scenes based on the provided scene ID.
     *
     * @param id The scene to play
     */
    public void switchScene(@NonNull int id) {
        if (currentScene == id) {
            return;
        }

        switch (id) {
            case R.layout.scene_routes:
                TransitionManager.go(routesScene);
                RoutesHandler.handleRoutes(
                        context,
                        rootView,
                        Data.routes
                );

                break;
            case R.layout.scene_route_deails:
                TransitionManager.go(routeDetailsScene);

                if (currentScene == R.layout.scene_routes) {
                    MapHandler.focusMapOnRoute();
                    MapHandler.setupRouteDirectionsAPI(context, rootView);
                }

                break;
            case R.layout.scene_points_of_interest:
                TransitionManager.go(pointsOfInterestScene);
                PointsOfInterestHandler.handlePointsOfInterest(
                        context,
                        rootView,
                        Data.selectedRoute.pointsOfInterest
                );

                break;
            case R.layout.scene_navigation:
                TransitionManager.go(navigationScene);
                NavigationHandler.startNavigation(
                        context,
                        rootView,
                        MapHandler.currentRouteObject
                );

                break;
        }

        currentScene = id;
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
                R.layout.scene_routes,
                getActivity()
        );

        routeDetailsScene = Scene.getSceneForLayout(
                (ViewGroup) rootView,
                R.layout.scene_route_deails,
                getActivity()
        );

        pointsOfInterestScene = Scene.getSceneForLayout(
                (ViewGroup) rootView,
                R.layout.scene_points_of_interest,
                getActivity()
        );

        navigationScene = Scene.getSceneForLayout(
                (ViewGroup) rootView,
                R.layout.scene_navigation,
                getActivity()
        );

        return view;
    }

    public static void onRouteRenderComplete(Context context, View rootView) {
        RouteDetailsHandler.handleRouteSelection(context, rootView);
    }
}
