package ca.kklee.comics;

import android.os.Environment;

import com.kklee.utilities.Logger;

import java.io.File;

/**
 * Created by Keith on 27/06/2014.
 */
public class FileUtil {

    public static File findFile(String file) {
        File sdCardRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File myDir = new File(sdCardRoot, AppConfig.APPDIRECTORY);
        if (!myDir.exists()) {
            myDir.mkdir();
        }
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
