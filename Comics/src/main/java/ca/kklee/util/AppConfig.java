package ca.kklee.util;

/**
 * Created by Keith on 08/06/2014.
 */
public class AppConfig {

//    private static AppConfig instance = null;
    private static boolean IS_DEBUGGING = true;
    private static boolean IS_LOGGING = false;

//    private AppConfig(){}

//    public synchronized static AppConfig getInstance() {
//        if (instance == null) {
//            instance = new AppConfig();
//        }
//        return instance;
//    }

    public static boolean IS_DEBUGGING() {
        return IS_DEBUGGING;
    }

    public static boolean IS_LOGGING() {
        return IS_LOGGING;
    }
}
