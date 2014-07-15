package ca.kklee.comics.comic;

import android.graphics.Bitmap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ca.kklee.comics.AppConfig;
import ca.kklee.comics.BitmapLoader;
import ca.kklee.util.Logger;

/**
 * Created by Keith on 04/06/2014.
 */
public class Comic {

    private String title;
    private String url;
    private String imgSrc;
    private Boolean enabled;
    private Bitmap bitmap;

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getBitmapFileName() {
        return BitmapLoader.findFile(title).getName();
    }

    public Bitmap getBitmap() {
        if (bitmap != null) {
            return bitmap;
        }
        File file = BitmapLoader.findFile(title);
        SimpleDateFormat spf = new SimpleDateFormat("yyMMdd");
        String today = spf.format(Calendar.getInstance().getTime());
        if (file != null && file.getName().equals(title + "_" + today + ".png")) {
            bitmap = BitmapLoader.loadBitmap(AppConfig.APPDIRECTORY + File.separator + title + "_" + today);
            Logger.d("", "Today's File found: " + title + "_" + today + ".png");
        } else if (file != null) {
            Logger.d("", "File found but not today's: " + title + "_" + today + ".png");
            file.delete();
        } else {
            Logger.d("", "File not found: " + title + "_" + today + ".png");
        }
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        BitmapLoader.saveBitmap(AppConfig.APPDIRECTORY + File.separator + title + "_" + new SimpleDateFormat("yyMMdd").format(Calendar.getInstance().getTime()), bitmap);
    }

    public void clearBitmap() {
        this.bitmap = null;
    }

    public String toString() {
        return title + " | " + url + " | " + imgSrc + " | " + enabled;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}