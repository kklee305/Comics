package ca.kklee.comics;

import android.media.MediaScannerConnection;
import android.net.Uri;

import java.io.File;

/**
 * Created by Keith on 27/06/2014.
 */
public class FileUtil {

    protected static File getMyDir() {
        File sdCardRoot = AppConfig.getContext().getExternalFilesDir(null);
        File myDir = new File(sdCardRoot, AppConfig.APPDIRECTORY);
        return myDir;
    }

    public static File findFile(String file) {
        File myDir = getMyDir();
        if (!myDir.exists()) {
            return null;
        }
        for (File f : myDir.listFiles()) {
            if (f.isFile()) {
                int separator = f.getName().lastIndexOf("_");
                if (separator < 0) separator = 0;
                String comicTitle = f.getName().substring(0, separator);
                if (comicTitle.equals(file))
                    return f;
            }
        }
        return null;
    }

    public static void clearDir() {
        for (File f : getMyDir().listFiles()) {
            f.delete();
        }
    }


    public static void scanFile(File file) {
        new SingleMediaScanner(file);
    }

    private static class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mediaScannerConnection;
        private File file;

        public SingleMediaScanner(File file) {
            this.mediaScannerConnection = new MediaScannerConnection(AppConfig.getContext(), this);
            this.file = file;
            mediaScannerConnection.connect();
        }

        @Override
        public void onMediaScannerConnected() {
            mediaScannerConnection.scanFile(file.getAbsolutePath(), null);
        }

        @Override
        public void onScanCompleted(String s, Uri uri) {
            mediaScannerConnection.disconnect();
        }
    }
}
