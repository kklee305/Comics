package ca.kklee.comics.loaders;

import android.graphics.Bitmap;
import android.view.View;

import org.jsoup.nodes.Document;

import java.net.URL;

import ca.kklee.comics.comic.ComicLoader;
import ca.kklee.util.Logger;

/**
 * Created by Keith on 05/06/2014.
 */
public class DilbertLoader extends ComicLoader {

    public DilbertLoader(View rootView, int id) {
        super(rootView, id);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return downloadDOM(strings[0]);
    }

    private Bitmap downloadDOM(String comicUrl) {
        Logger.d("", "Attempt DOM Retrieval: " + comicUrl);
        Document dom = super.getDom(comicUrl);
        if (dom == null) {
            return null;
        }
        URL imageUrl = null;
        try {
            imageUrl = new URL(dom.select("Body").select("img[src*=amuniversal]").attr("src").toString());
        } catch (Exception e) {
            Logger.e("Failed to create url: " + e.toString());
        }
        return downloadImage(imageUrl);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        loadImage(bitmap);
    }
}
