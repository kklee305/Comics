package ca.kklee.util;

import android.app.Application;

/**
 * Created by Keith on 08/06/2014.
 */
public class Config extends Application {

    //    private static Config instance = null;
    private static boolean IS_DEBUGGING = true;
    private static boolean IS_LOGGING = true;

//    public static Context getContext() {
//        return instance;
//    }

    public static boolean IS_DEBUGGING() {
        return IS_DEBUGGING;
    }

    public static boolean IS_LOGGING() {
        return IS_LOGGING;
    }

//    @Override
//    public void onCreate() {
//        instance = this;
//        super.onCreate();
//    }
}
