package ca.kklee.util;

import android.os.Environment;
import android.util.Log;

import ca.kklee.comics.AppConfig;

/**
 * Created by Keith on 10/06/2014.
 */
public class Logger {

    private static final boolean IS_LOGGING = Config.IS_LOGGING();

    public static void d(String tag, String message) {
        if (!AppConfig.IS_LOGGING()) return;
        Log.d(tag, message);
    }

    public static void e(String message) {
        Log.e("ERROR", message);
        logToFile("ERROR", message);
    }

    private static void logToFile(String tag, String message) {

    }

}