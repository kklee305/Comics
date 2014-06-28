package ca.kklee.comics.loaders;

import android.graphics.Bitmap;
import android.view.View;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ca.kklee.comics.ComicCollection;
import ca.kklee.util.Logger;

/**
 * Created by Keith on 05/06/2014.
 */
public class PeanutsLoader extends ComicLoader<String> {

    public PeanutsLoader(View rootView, int id) {
        super(rootView, id);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return downloadDOM(strings[0]);
    }

    private Bitmap downloadDOM(String comicUrl) {
        Logger.d("", "Attempt DOM Retrieval: " + comicUrl);
        try {
            URL imageUrl = null;
            if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == 1) {
                imageUrl = new URL(comicUrl + getTodayDateWithFormat("Sundays/pe_c[Date].jpg"));
            } else {
                imageUrl = new URL(comicUrl + getTodayDateWithFormat("daily/pe_c[Date].jpg"));
            }
            return downloadImage(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
        }
        return null;
    }

    public String getTodayDateWithFormat(String string) {
        SimpleDateFormat spf = new SimpleDateFormat(ComicCollection.getInstance().getComics()[id].getDateFormat());
        return string.replace("[Date]",spf.format(Calendar.getInstance().getTime()));
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        loadImage(bitmap);
    }
}
