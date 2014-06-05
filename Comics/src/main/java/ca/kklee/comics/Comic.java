package ca.kklee.comics;

/**
 * Created by Keith on 04/06/2014.
 */
public class Comic {
    private String title;
    private String imgSrc;
    private String update;
    private String time;

    public String toString(){
        return title + " | " + imgSrc + " | " + update + " | " + time;
    }

    public String getTitle() {
        return title;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public String getUpdate() {
        return update;
    }

    public String getTime() {
        return time;
    }
}