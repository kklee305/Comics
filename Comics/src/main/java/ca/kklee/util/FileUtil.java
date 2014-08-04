package ca.kklee.util;

import android.os.Environment;

import java.io.File;

import ca.kklee.comics.AppConfig;

/**
 * Created by Keith on 27/06/2014.
 */
public class FileUtil {

    public static File findFile(String file) {
        File sdCardRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File myDir = new File(sdCardRoot, AppConfig.APPDIRECTORY);
        for (File f : myDir.listFiles()) {
            if (f.isFile()) {
                int seperator = f.getName().lastIndexOf("_");
                String comicTitle = f.getName().substring(0, seperator);
                if (comicTitle.equals(file))
                    return f;
            }
        }
        return null;
    }

    public static void clearDir() {
        File sdCardRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File myDir = new File(sdCardRoot, AppConfig.APPDIRECTORY);
        for (File f : myDir.listFiles()) {
            f.delete();
        }
    }

}
