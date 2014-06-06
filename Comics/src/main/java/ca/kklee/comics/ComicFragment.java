package ca.kklee.comics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.net.URL;

/**
 * Created by Keith on 02/06/2014.
 */
public class ComicFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.comic_fragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
        ProgressBar loading = (ProgressBar) rootView.findViewById(R.id.loading);

        ComicLoader comicLoader = new ComicLoader(loading, imageView);
        URL url;
        try{
            url = new URL(ComicCollection.getInstance().getComics()[getArguments().getInt("ID")].getImgSrc());
        } catch (Exception e) {
            Log.e("ERROR", "Failed to create url: "+ e.toString());
            return rootView;
        }
        comicLoader.execute(url);

        return rootView;
    }
}
