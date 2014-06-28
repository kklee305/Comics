package ca.kklee.comics;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.net.URL;

import ca.kklee.comics.loaders.AbstractComicLoaderFactory;

/**
 * Created by Keith on 02/06/2014.
 */
public class ComicFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int id = getArguments().getInt("ID");

        View rootView = inflater.inflate(R.layout.comic_fragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
        ProgressBar loading = (ProgressBar) rootView.findViewById(R.id.loading);

        Bitmap bitmap = ComicCollection.getInstance().getComics()[id].getBitmap();

        if (bitmap == null) {
            AbstractComicLoaderFactory.getLoader(rootView, id).execute(getStringURL());
        } else {
            imageView.setImageBitmap(bitmap);
            loading.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private URL getURL() {
        try {
            return new URL(ComicCollection.getInstance().getComics()[getArguments().getInt("ID")].getImgSrc());
        } catch (Exception e) {
            Log.e("ERROR", "Failed to create url: " + e.toString());
            return null;
        }
    }

    private String getStringURL() {
        return ComicCollection.getInstance().getComics()[getArguments().getInt("ID")].getImgSrc();
    }
}
