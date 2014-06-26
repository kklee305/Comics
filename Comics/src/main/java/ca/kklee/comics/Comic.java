package ca.kklee.comics;

import android.graphics.Bitmap;

/**
 * Created by Keith on 04/06/2014.
 */
public class Comic {

    private String title;
    private String imgSrc;
    private String dateFormat;
    private String update;
    private String time;
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
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String toString() {
        return title + " | " + imgSrc + " | " + update + " | " + time;
    }
}