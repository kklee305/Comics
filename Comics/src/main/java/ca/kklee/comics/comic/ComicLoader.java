package ca.kklee.comics.comic;

import android.content.Context;
import android.content.SharedPreferences;
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

import ca.kklee.comics.AppConfig;
import ca.kklee.comics.R;
import ca.kklee.comics.SharedPrefConstants;
import ca.kklee.comics.scheduletask.NewComicListener;

/**
 * Created by Keith on 05/06/2014.
 */
public class ComicLoader extends AsyncTask<String, Void, Bitmap> {

    private enum ResultCode {
        NOUPDATE, UPDATED, ERROR
    }
    private int id;
    private NewComicListener newComicListener;
    private View rootView;
    private String imageUrlString;
    private ResultCode result = ResultCode.ERROR;

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
            result = ResultCode.NOUPDATE;
            return null;
        }
        imageUrlString = imageUrl.toString();
        return downloadImage(imageUrl);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            ComicCollection.getInstance().getComics()[id].saveBitmap(bitmap, imageUrlString.hashCode());
            result = ResultCode.UPDATED;
            if (rootView != null) {
                ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.loading).setVisibility(View.GONE);
            }
        }
        newComicResponse(result);
        if (rootView != null && result.equals(ResultCode.ERROR)) {
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
            Logger.e("Failed to create url: %s from dom for " + ComicCollection.getInstance().getComics()[id].getFullTitle(), e, imageUrlFromDOM);
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

            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Logger.i("Success DLImage: " + url);
                return bitmap;
            } catch (OutOfMemoryError e) {
                Logger.e("OutOfMemoryError while decoding bitmap, attempting downscale", e);
//            return downscaleBitmap(url, new BufferedInputStream(urlConnection.getInputStream()));
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            Logger.e("IOException: ", e);
            e.printStackTrace();
        }
        return null;
    }

//    private Bitmap downscaleBitmap(URL url, InputStream inputStream) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(inputStream, null, options);
//        int bitmapW = options.outWidth;
//        int bitmapH = options.outHeight;
//        Logger.d("bitmap width: %d, height: %d", bitmapW, bitmapH);
//
//
//        int scaleFactor = (int) Math.max(1.0, Math.min((double) bitmapW / (double) 2, (double) bitmapH / (double) 2));    //1, 2, 3, 4, 5, 6, ...
//        scaleFactor = (int) Math.pow(2.0, Math.floor(Math.log((double) scaleFactor) / Math.log(2.0)));               //1, 2, 4, 8, ...
//
//        options.inJustDecodeBounds = false;
//        options.inSampleSize = scaleFactor;
//        options.inPurgeable = true;
//
//        Bitmap bitmap = null;
//        do {
//            try {
//                Logger.d("scaleFactor: " + scaleFactor);
//                scaleFactor *= 2;
//                bitmap = BitmapFactory.decodeStream(inputStream, null, options);
//            } catch (OutOfMemoryError e) {
//                options.inSampleSize = scaleFactor;
//
//            }
//        } while (bitmap == null && scaleFactor <= 256);
//        if (bitmap == null) {
//            Logger.d("OutOfMemoryError: downscale failed");
//        } else {
//            Logger.i("Success DLImage: " + url);
//        }
//
//        return bitmap;
//    }

    private void newComicResponse(ResultCode response) {
        String title = ComicCollection.getInstance().getComics()[id].getTitle();
        if (newComicListener != null) {
            newComicListener.onDomCheckCompleted(title);
        }
        Context context = AppConfig.getContext();
        SharedPreferences prefForNew = context.getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0);
        SharedPreferences.Editor editorForNew = prefForNew.edit();
        SharedPreferences prefForError = context.getSharedPreferences(SharedPrefConstants.COMICERRORFLAG, 0);
        SharedPreferences.Editor editorForError = prefForError.edit();
        SharedPreferences prefForTime = context.getSharedPreferences(SharedPrefConstants.COMICUPDATETIME, 0);
        SharedPreferences.Editor editorForTime = prefForTime.edit();
        switch (response) {
            case UPDATED:
                editorForNew.putBoolean(title, true);
                editorForNew.commit();
                editorForTime.putLong(title, System.currentTimeMillis());
                editorForTime.apply();
            case NOUPDATE:
                editorForError.putBoolean(title, false);
                break;
            case ERROR:
                editorForError.putBoolean(title, true);
                break;
        }
        editorForError.apply();
    }
}
