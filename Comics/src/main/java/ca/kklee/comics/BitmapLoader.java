package ca.kklee.comics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;

import com.kklee.utilities.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ca.kklee.comics.comic.Comic;
import ca.kklee.comics.comic.ComicCollection;

/**
 * Created by Keith on 27/06/2014.
 */
public class BitmapLoader extends FileUtil {

    public static void saveBitmap(String fileName, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + File.separator + fileName + ".png");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            scanImage(f);
            fo.close();
            bytes.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap loadBitmap(File file) {
        try {
            return BitmapFactory.decodeFile(file.getPath());
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        return null;
    }

    public static void clearBitmap() {
        for (Comic c : ComicCollection.getInstance().getComics()) {
            c.clearBitmap();
        }
        clearDir();
    }

    private static void scanImage(File file) {
        new SingleMediaScanner(file);
    }

    private static class SingleMediaScanner implements MediaScannerConnectionClient {

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
