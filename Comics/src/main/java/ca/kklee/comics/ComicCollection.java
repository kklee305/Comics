package ca.kklee.comics;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;

import ca.kklee.util.StringUtil;

/**
 * Created by Keith on 05/06/2014.
 */
public class ComicCollection {

    private static ComicCollection instance = null;
    private Comic[] comics = null;

    private ComicCollection() {
    }

    public static synchronized ComicCollection getInstance() {
        if (instance == null) {
            instance = new ComicCollection();
        }
        return instance;
    }

    public Comic[] getComics() {
        return comics;
    }

    public void setComics(Activity activity) {
        comics = parse(activity);
    }

    public Comic[] parse(Activity activity) {
        String inputString = "";

        try {
            InputStream inputFile = activity.getAssets().open("comic_collection.json");
            inputString = StringUtil.convertStreamToString(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR", e.toString());
        }

        if (inputString.isEmpty())
            return null;
        Gson gson = new Gson();
        return gson.fromJson(inputString, Comic[].class);
    }
}
