package a2.mobile.mobileapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import a2.mobile.mobileapp.R;
import a2.mobile.mobileapp.data.Data;
import a2.mobile.mobileapp.enums.Scenes;
import a2.mobile.mobileapp.handlers.MapHandler;
import a2.mobile.mobileapp.handlers.NavigationHandler;
import a2.mobile.mobileapp.handlers.PointsOfInterestHandler;
import a2.mobile.mobileapp.handlers.RouteDetailsHandler;
import a2.mobile.mobileapp.handlers.RoutesHandler;
import a2.mobile.mobileapp.utils.MapUtils;
import timber.log.Timber;

public class MainActivityFragment extends Fragment {

    private Context context;
    private View rootView;

    private Scene routesScene;
    private Scene routeDetailsScene;
    private Scene pointsOfInterestScene;

    private Scenes currentScene;

    public MainActivityFragment(Context context) {
        this.context = context;
    }

    public void goBack() {
        switch (currentScene) {
            case routeDetails:
                Timber.e("ROUTE DETIALS SCENE TO BE LOADED");
                switchScene(Scenes.routes);

                // TODO: Only clear route rendering if there is no active navigation.
                MapUtils.clearRouteMarkers(context);

                break;
            case pointsOfInterest:
            case navigation:
                switchScene(Scenes.routeDetails);

                break;

        }
    }

    /**
     * Switch Scene based on the provided scene ID.
     *
     * @param id The scene to play
     */
    public void switchScene(Scenes id) {
        if (currentScene == id) {
            return;
        }

        switch (id) {
            case routes:
                TransitionManager.go(routesScene);
                RoutesHandler.handleRoutes(
                        context,
                        rootView,
                        Data.routes
                );

                break;
            case routeDetails:
                TransitionManager.go(routeDetailsScene);

                if (currentScene != Scenes.pointsOfInterest) {
                    MapHandler.focusMapOnRoute(context);
                }

                RouteDetailsHandler.handleRouteSelection(context, rootView);

                break;
            case pointsOfInterest:
                TransitionManager.go(pointsOfInterestScene);
                PointsOfInterestHandler.handlePointsOfInterest(
                        context,
                        Data.selectedRoute.pointsOfInterest
                );

                break;
            case navigation:
                ((Activity) context).runOnUiThread(() ->
                        NavigationHandler.startNavigation((Activity) context)
                );

                id = Scenes.routeDetails;
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

        return view;
    }
}
