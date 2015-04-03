package ca.kklee.comics.comic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;

import com.kklee.utilities.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
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

    private String getImageUrlFromDOM(Document dom) {
        switch (ComicCollection.getInstance().getComics()[id].getFullTitle()) {
            case "Garfield":
                return dom.getElementById("home_comic").select("img[src]").attr("src");
            case "XKCD":
                return "http:" + dom.getElementById("comic").select("img[src]").attr("src");
            case "Nerf Now":
                return dom.getElementById("comic").select("img[src]").attr("src");
            case "Saturday Morning Breakfast Cereal":
                return ComicCollection.getInstance().getComics()[id].getUrl() + dom.getElementById("comicbody").select("img[src]").attr("src");
            case "Cyanide & Happiness":
                return dom.getElementById("posts").select("article").select("img[src]").attr("src");
            case "MANvsMAGIC":
                return ComicCollection.getInstance().getComics()[id].getUrl() + dom.select("main").select("img[src]").attr("src");
            case "Dilbert":
                return dom.select("div[class*=img-comic-container").select("img[src]").attr("src");
            case "Extra Fabulous Comics":
                return dom.getElementById("comic").select("img[src]").attr("src");
            case "Penny Arcade":
                return dom.getElementById("comicFrame").select("img[src]").attr("src");
            case "Peanuts":
            case "Calvin and Hobbes":
            case "2 Cows and a Chicken":
            case "Wizard of Id":
            case "Get Fuzzy":
            case "Dilbert Classics":
            case "Marmaduke":
                return dom.select("Body").select("img[src*=amuniversal]").attr("src");
            default:
                return "error";
        }
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
            if (rootView == null)
                return;
            ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.loading).setVisibility(View.GONE);
        }
        newComicResponse(return_code);
    }

    private URL downloadDom(String comicUrl) {
        Logger.d("", "Attempt DOM Retrieval: " + comicUrl);
        if (!Patterns.WEB_URL.matcher(comicUrl).matches()) {
            Logger.e("Base URL not valid: " + comicUrl);
            return null;
        }
        Document dom = getDom(comicUrl);
        if (dom == null) {
            return null;
        }
        URL imageUrl = null;
        String imageUrlFromDOM = getImageUrlFromDOM(dom);
        try {
            imageUrl = new URL(imageUrlFromDOM);
        } catch (Exception e) {
            Logger.e("Failed to create url: " + imageUrlFromDOM + " exception: " + e.toString());
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
            Logger.e("Error getting dom: " + e.getMessage());
        }
        return null;
    }

    private Bitmap downloadImage(URL url) {
        Logger.d("", "Attempt DL image: " + url);
        if (url == null) {
            return null;
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url.toString());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Logger.e("ERROR: downloadImage " + statusCode + " : " + url);
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Logger.d("", "Success DLImage: " + url);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (ClientProtocolException e) {
            Logger.e("ClientProtocolException: " + e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Logger.e("IOException: " + e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return null;
    }

    private void newComicResponse(int response) {
        if (newComicListener != null) {
            newComicListener.onDomCheckCompleted(response, ComicCollection.getInstance().getComics()[id].getTitle());
        }
    }
}
