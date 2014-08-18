package ca.kklee.comics;

import android.app.Application;
import android.content.Context;

/**
 * Created by Keith on 27/06/2014.
 */
public class AppConfig extends Application {
    public static final String APPDIRECTORY = "Daily Comics";

    private static AppConfig instance;
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
