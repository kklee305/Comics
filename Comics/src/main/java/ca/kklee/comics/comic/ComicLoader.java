package ca.kklee.comics.comic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.kklee.utilities.Logger;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ca.kklee.comics.R;
import ca.kklee.comics.scheduletask.NewComicListener;

/**
 * Created by Keith on 05/06/2014.
 */
public class ComicLoader extends AsyncTask<String, Void, Bitmap> {

    protected int id;
    private NewComicListener newComicListener;
    private View rootView;
    private String imageUrlString;
    private int return_code = 2;

    public ComicLoader(View rootView, int id, NewComicListener newComicListener) {
        this.rootView = rootView;
        this.id = id;
        this.newComicListener = newComicListener;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Comic comic = ComicCollection.getInstance().getComics()[id];
        URL imageUrl = downloadDom(strings[0]);
        if (imageUrl == null) {
            return null;
        }
        int newFileCode = imageUrl.toString().hashCode();
        int oldFileCode = comic.getFileHashCode();
        if (newFileCode == oldFileCode) {
            return_code = 0;
            return null;
        }
        imageUrlString = imageUrl.toString();
        return downloadImage(imageUrl);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            ComicCollection.getInstance().getComics()[id].saveBitmap(bitmap, imageUrlString.hashCode());
            return_code = 1;
            if (rootView != null) {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.loading).setVisibility(View.GONE);
            }
        }
        newComicResponse(return_code);
        if (rootView != null) {
            ImageView errorView = (ImageView) rootView.findViewById(R.id.error_view);
            errorView.setBackground(rootView.getResources().getDrawable(R.drawable.error));
            errorView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }

    private URL downloadDom(String comicUrl) {
        Logger.i("Attempt DOM Retrieval: " + comicUrl);
        if (!Patterns.WEB_URL.matcher(comicUrl).matches()) {
            Logger.e("Base URL not valid: " + comicUrl);
            return null;
        }
        Document dom = getDom(comicUrl);
        if (dom == null) {
            return null;
        }
        URL imageUrl = null;
        String imageUrlFromDOM = ComicDOMDictionary.getImageUrlFromDOM(dom, id);
        try {
            imageUrl = new URL(imageUrlFromDOM);
        } catch (Exception e) {
            Logger.e("Failed to create url from dom for " + ComicCollection.getInstance().getComics()[id].getFullTitle(), e);
        }
        return imageUrl;
    }

    private Document getDom(String url) {
        try {
            Connection.Response response = Jsoup.connect(url).timeout(10000).execute();
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                return response.parse();
            } else {
                Logger.e("Error getting dom: Response code " + statusCode);
            }
        } catch (IOException e) {
            Logger.e("Exception getting dom: ", e);
        }
        return null;
    }

    private Bitmap downloadImage(URL url) {
        Logger.i("Attempt DL image: " + url);
        if (url == null || !URLUtil.isValidUrl(url.toString())) {
            Logger.e("Download image url not valid: " + url);
            return null;
        }

        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Logger.e("Http response code not ok: " + responseCode + " ||| " + url);
                return null;
            }

            InputStream inputStream = null;
            try {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Logger.i("Success DLImage: " + url);
                return bitmap;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (IOException e) {
            Logger.e("IOException: ", e);
            e.printStackTrace();
        }
        return null;
    }

    private void newComicResponse(int response) {
        if (newComicListener != null) {
            newComicListener.onDomCheckCompleted(response, ComicCollection.getInstance().getComics()[id].getTitle());
        }
    }
}
