package ca.kklee.comics.comic;

import android.graphics.Bitmap;

import com.kklee.utilities.Logger;

import java.io.File;
import java.io.IOException;

import ca.kklee.comics.BitmapLoader;
import ca.kklee.comics.FileUtil;

/**
 * Created by Keith on 04/06/2014.
 */
public class Comic {

    private String title;
    private String shortForm;
    private String url;
    private Boolean enabled;
    private Bitmap bitmap;

    public String getTitle() {
        if (shortForm == null || shortForm.equals(""))
            return title;
        return shortForm;
    }

    public String getFullTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            bitmap = getBitmapFromFile();
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getFileHashCode() {
        File file = BitmapLoader.findFile(title);
        if (file == null) {
            return 0;
        }
        String code = file.getName();
        code = code.replace(title + "_", "").replace(".png", "");
        return Integer.parseInt(code);
    }

    public Bitmap getBitmapFromFile() {
        File file = BitmapLoader.findFile(title);
        if (file != null) {
            return BitmapLoader.loadBitmap(file);
        } else {
            Logger.w("File not found: " + title);
            return null;
        }
    }

    public void saveBitmap(Bitmap bitmap, int hashCode) {
        setBitmap(bitmap);
        File file = BitmapLoader.findFile(title);
        if (file != null) {
            try {
                if (!file.getCanonicalFile().delete())
                    Logger.wtf("File for %s not deleted!", title);
                FileUtil.scanFile(file);
            } catch (IOException e) {
                Logger.e("IOException", e);
            }
        }
        BitmapLoader.saveBitmap(title + "_" + hashCode, bitmap);
    }

    public void clearBitmap() {
        this.bitmap = null;
    }

    public String toString() {
        return title + " | " + url + " | " + " | " + enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}