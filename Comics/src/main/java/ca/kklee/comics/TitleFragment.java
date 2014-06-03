package ca.kklee.comics;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Keith on 02/06/2014.
 */
public class TitleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.title_fragment,container,false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_view);

        return rootView;
    }
}
