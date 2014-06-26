package ca.kklee.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import ca.kklee.comics.R;

/**
 * Created by Keith on 02/06/2014.
 */
public class DebuggingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.debugging_fragment, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.text_view);

        Log.d("DEBUGGING", "testing dom");
        ComicDOM comicDom = new ComicDOM(textView);
        try {
            comicDom.execute(new URL("http://xkcd.com/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("ERROR",e.toString());
        }

        return rootView;
    }
}
