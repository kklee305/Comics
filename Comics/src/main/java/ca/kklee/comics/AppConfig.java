package ca.kklee.comics;

import android.app.Application;
import android.content.Context;

/**
 * Created by Keith on 27/06/2014.
 */
public class AppConfig extends Application {
    public static final String APPDIRECTORY = "Daily Comics";

    private static AppConfig instance;

    private static boolean DEBUGGABLE = BuildConfig.DEBUG;
    private static boolean IS_LOGGING = true & DEBUGGABLE;
    private static boolean DEMOMODE = !DEBUGGABLE;

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static boolean DEBUGGABLE() {
        return DEBUGGABLE;
    }

    public static boolean IS_LOGGING() {
        return IS_LOGGING;
    }

    public static boolean DEMOMODE() {
        return DEMOMODE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
