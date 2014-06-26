package ca.kklee.util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Keith on 05/06/2014.
 */
public class ComicDOM extends AsyncTask<URL, Void, String> {

    private TextView textView;

    public ComicDOM(TextView textView) {
        this.textView = textView;
    }

    @Override
    protected String doInBackground(URL... urls) {
        return downloadDOM(urls[0]);
    }

    private String downloadDOM(URL url) {
        Log.d("", "Attempt DOM Retrieval: " + url);
//        HttpClient httpclient = new DefaultHttpClient();
//        HttpGet httpget = new HttpGet(url.toString());
//        HttpResponse response = null;
//        try {
//            response = httpclient.execute(httpget);
//            int statusCode = response.getStatusLine().getStatusCode();
//            if (statusCode != HttpStatus.SC_OK) {
//                Log.e("", "ERROR: " + statusCode + " : " + url);
//                return null;
//            }
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream inputStream = null;
//                try {
//                    inputStream = entity.getContent();
//                    String result = StringUtil.convertStreamToString(inputStream);
//                    return result;
//                } finally {
//                    if (inputStream != null) {
//                        inputStream.close();
//                    }
//                    entity.consumeContent();
//                }
//            }
//        } catch (ClientProtocolException e) {
//            Log.e("ClientProtocolException", e.getLocalizedMessage());
//            e.printStackTrace();
//        } catch (IOException e) {
//            Log.e("IOException", e.getLocalizedMessage());
//            e.printStackTrace();
//        } finally {
//            httpclient.getConnectionManager().shutdown();
//            Log.d("", "Success DOM Retrieval: " + url);
//        }
//        return null;

        try {
            Document dom = Jsoup.connect("http://xkcd.com").get();
            dom.getElementById("comic").toString();
            return dom.getElementById("comic").select("img[src$=.png]").attr("src").toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String domString) {
        super.onPostExecute(domString);

        if (!domString.isEmpty()) {
            textView.setText(domString);
        }

    }

}
