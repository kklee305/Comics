package ca.kklee.comics.loaders;

import android.graphics.Bitmap;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

import ca.kklee.util.Logger;

/**
 * Created by Keith on 05/06/2014.
 */
public class NerfNowLoader extends ComicLoader {

    public NerfNowLoader(View rootView, int id) {
        super(rootView, id);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return downloadDOM(strings[0]);
    }

    private Bitmap downloadDOM(String comicUrl) {
        Logger.d("", "Attempt DOM Retrieval: " + comicUrl);
        try {
            Document dom = Jsoup.connect(comicUrl).get();
            URL imageUrl = null;
            try{
                imageUrl = new URL(dom.getElementById("comic").select("img[src]").attr("src").toString());
            } catch (Exception e) {
                Logger.e("Failed to create url: "+ e.toString());
            }
            return downloadImage(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        loadImage(bitmap);
    }
}
