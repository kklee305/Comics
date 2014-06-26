package ca.kklee.comics.loaders;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ca.kklee.comics.ComicCollection;
import ca.kklee.comics.R;
import ca.kklee.util.Logger;

/**
 * Created by Keith on 05/06/2014.
 */
public abstract class ComicLoader<T> extends AsyncTask<T, Void, Bitmap> {

    private View rootView;
    private int id;

    public ComicLoader(View rootView, int id) {
        this.rootView = rootView;
        this.id = id;
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

    protected void loadImage(Bitmap bitmap) {
        if (bitmap != null) {
            ComicCollection.getInstance().getComics()[id].setBitmap(bitmap);

            ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.loading).setVisibility(View.GONE);
        }
    }
}
