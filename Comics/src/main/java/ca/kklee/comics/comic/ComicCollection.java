package ca.kklee.comics.comic;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kklee.utilities.Logger;
import com.kklee.utilities.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ca.kklee.comics.AppConfig;

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

    public void setComics(Context context) {
        comics = parse(context);
        if (comics == null) {
            Logger.wtf("FATAL ERROR: json not parsed");
        }
        checkComicEnabled();
    }

    public Comic[] parse(Context context) {
        String inputString = "";
        String jsonFile = AppConfig.DEMOMODE() ? "comic_collection_truncated.json" : "comic_collection.json";
        try {
            InputStream inputFile = context.getAssets().open(jsonFile);
            inputString = StringUtil.convertStreamToString(inputFile);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR parsing json", e.toString());
        }

        if (inputString.isEmpty())
            return null;
        Gson gson = new Gson();
        return gson.fromJson(inputString, Comic[].class);
    }

    private void checkComicEnabled() {
        List<Comic> list = new LinkedList<Comic>(Arrays.asList(comics));
        for (int i = 0; i < comics.length; i++) {
            if (!comics[i].getEnabled())
                list.remove(comics[i]);
        }
        comics = list.toArray(new Comic[list.size()]);
    }

    public String[] getFullTitleArray() {
        List<String> list = new ArrayList<String>();
        for (Comic c : ComicCollection.getInstance().getComics()) {
            list.add(c.getFullTitle());
        }
        return list.toArray(new String[list.size()]);
    }

    public void clearAllBitmap() {
        if (comics != null)
            for (Comic c : comics) c.clearBitmap();
    }

}
