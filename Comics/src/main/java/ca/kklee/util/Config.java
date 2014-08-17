package ca.kklee.util;

import android.app.Application;
import android.content.Context;

/**
 * Created by Keith on 08/06/2014.
 */
public class Config extends Application {

    private static Config instance;
    private static boolean IS_DEBUGGING = true;
    private static boolean IS_LOGGING = true;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static boolean IS_DEBUGGING() {
        return IS_DEBUGGING;
    }

    public static boolean IS_LOGGING() {
        return IS_LOGGING;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
