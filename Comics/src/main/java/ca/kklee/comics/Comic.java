package ca.kklee.comics;

import android.graphics.Bitmap;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ca.kklee.util.Logger;

/**
 * Created by Keith on 04/06/2014.
 */
public class Comic {

    private String title;
    private String imgSrc;
    private String dateFormat;
    private String update;
    private String time;
    private Boolean enabled;
    private Bitmap bitmap;

    public String getTitle() {
        return title;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getUpdate() {
        return update;
    }

    public String getTime() {
        return time;
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
            Logger.d("","Today's File found: " + title + "_" + today + ".png");
        } else if (file != null) {
            Logger.d("","File found but not today's: " + title + "_" + today + ".png");
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
        return title + " | " + imgSrc + " | " + update + " | " + time;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}