package ca.kklee.comics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

import ca.kklee.comics.comic.ComicCollection;

/**
 * Created by Keith on 05/06/2014.
 */
public class ImageLoader extends AsyncTask<URL, Void, Bitmap> {

    private ProgressBar loading;
    private ImageView imageView;
    private int id;

    public ImageLoader(ProgressBar loading, ImageView imageView, int id) {
        this.loading = loading;
        this.imageView = imageView;
        this.id = id;
    }

    @Override
    protected Bitmap doInBackground(URL... urls) {
        return downloadImage(urls[0]);
    }

    private Bitmap downloadImage(URL url) {
//        Log.d("", "Attempt DL image: " + url);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url.toString());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.e("", "ERROR: downloadImage " + statusCode + " : " + url);
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    Log.d("", "Success DLImage: " + url);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", e.getLocalizedMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("IOException", e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap != null) {
            ComicCollection.getInstance().getComics()[id].setBitmap(bitmap);
            imageView.setImageBitmap(bitmap);
            loading.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
        }

    }
}