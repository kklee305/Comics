package ca.kklee.comics.viewpager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.kklee.utilities.ConnectionUtil;

import ca.kklee.comics.HomeActivity;
import ca.kklee.comics.R;
import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.comics.comic.ComicLoader;

/**
 * Created by Keith on 02/06/2014.
 */
public class ComicFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        int id = getArguments().getInt("ID");

        View rootView = inflater.inflate(R.layout.comic_fragment, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);
        ImageView errorView = (ImageView) rootView.findViewById(R.id.error_view);
        ProgressBar loading = (ProgressBar) rootView.findViewById(R.id.loading);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View decorView = getActivity().getWindow().getDecorView();
                if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    HomeActivity.hideUI(decorView);
                } else {
                    HomeActivity.showUI(decorView);
                }
            }
        };

        rootView.setOnClickListener(onClickListener);
        imageView.setOnClickListener(onClickListener);
        errorView.setOnClickListener(onClickListener);

        Bitmap bitmap = ComicCollection.getInstance().getComics()[id].getBitmap();

        if (bitmap == null) {
            if (!ConnectionUtil.isOnline(getActivity())) {
                errorView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_wifi));
                errorView.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
            } else {
                new ComicLoader(rootView, id, null).execute(getStringURL());
            }
        } else {
            imageView.setImageBitmap(bitmap);
            loading.setVisibility(View.GONE);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    private String getStringURL() {
        return ComicCollection.getInstance().getComics()[getArguments().getInt("ID")].getUrl();
    }
}
