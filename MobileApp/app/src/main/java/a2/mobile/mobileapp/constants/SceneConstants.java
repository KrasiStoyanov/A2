package a2.mobile.mobileapp.constants;

import java.util.Random;

import a2.mobile.mobileapp.enums.Scenes;

public class SceneConstants {
    public static final Scenes DEFAULT_SCENE_TO_LOAD = Scenes.routes;
    public static final int REQUEST_CODE_SAVED_INSTANCES = new Random().nextInt(10000);
}
