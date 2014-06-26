package ca.kklee.util;

import android.util.Log;

/**
 * Created by Keith on 10/06/2014.
 */
public class Logger {

    public static void d(String tag,String message) {
        if (!AppConfig.IS_LOGGING()) return;
        Log.d(tag,message);
    }

    public static void e(String message) {
        if (!AppConfig.IS_LOGGING()) return;
        Log.e("ERROR", message);
        logToFile("ERROR",message);
    }

    private static void logToFile(String tag, String message) {
        
    }

}