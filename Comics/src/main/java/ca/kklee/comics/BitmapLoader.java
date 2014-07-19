package ca.kklee.comics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import ca.kklee.comics.comic.Comic;
import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.util.FileUtil;
import ca.kklee.util.Logger;

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

            fo.close();
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

}
