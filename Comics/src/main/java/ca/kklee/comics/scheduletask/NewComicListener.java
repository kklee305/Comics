package ca.kklee.comics.scheduletask;

/**
 * Created by Keith on 18/07/2014.
 */
public interface NewComicListener {

    enum ResponseCode {
        NOUPDATE, UPDATED, ERROR
    }

    void onDomCheckCompleted(ResponseCode response, String title);

}
