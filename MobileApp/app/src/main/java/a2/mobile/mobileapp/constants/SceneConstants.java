package a2.mobile.mobileapp.constants;

import java.util.Random;

import a2.mobile.mobileapp.R;

public class SceneConstants {
    public static final int DEFAULT_SCENE_TO_LOAD = R.layout.scene_routes;
    public static final String CURRENT_SCENE_SAVED_INSTANCES = "CURRENT_SCENE";
    public static final int REQUEST_CODE_SAVED_INSTANCES = new Random().nextInt(10000);
}
