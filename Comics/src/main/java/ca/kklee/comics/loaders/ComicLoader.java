package ca.kklee.comics.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

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

import ca.kklee.comics.ComicCollection;
import ca.kklee.comics.R;
import ca.kklee.util.Logger;

/**
 * Created by Keith on 05/06/2014.
 */
public abstract class ComicLoader extends AsyncTask<String, Void, Bitmap> {

    protected int id;
    private View rootView;

    public ComicLoader(View rootView, int id) {
        this.rootView = rootView;
        this.id = id;
    }

    protected Document getDom(String url) {
        try {
            Connection.Response response = Jsoup.connect(url).timeout(10000).execute();
            int statusCode = response.statusCode();
            if (statusCode == 200) {
                return response.parse();
            }
        } catch (IOException e) {
            Logger.e("Error getting dom: " + e.getMessage());
        }
        return null;
    }

    protected Bitmap downloadImage(URL url) {
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
                    bitmap = checkBitmapSize(bitmap);
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

    private Bitmap checkBitmapSize(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int maxSize = 4096;
        if (width <= maxSize && height <= maxSize) return bitmap;
        Logger.d("Image Pre-resize", "Width: " + width + " | Height: " + height);
        if (width > height) {
            double ratio = width / maxSize;
            width = maxSize;
            height = (int) (height / ratio);
        } else if (height > width) {
            double ratio = height / maxSize;
            height = maxSize;
            width = (int) (width / ratio);
        } else {
            width = height = maxSize;
        }
        Logger.d("Image Resize", "Width: " + width + " | Height: " + height);
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    protected void loadImage(Bitmap bitmap) {
        if (bitmap != null) {
            ComicCollection.getInstance().getComics()[id].setBitmap(bitmap);
            if (rootView == null)
                return;
            ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.loading).setVisibility(View.GONE);
        } else {
            if (rootView == null)
                return;
            ImageView errorView = (ImageView) rootView.findViewById(R.id.error_view);
            errorView.setImageBitmap(BitmapFactory.decodeResource(rootView.getResources(), R.drawable.error));
            errorView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }
}
