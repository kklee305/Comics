package ca.kklee.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.kklee.comics.R;

/**
 * Created by Keith on 02/06/2014.
 */
public class DebuggingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.debugging_fragment, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.text_view);

        return rootView;
    }
}
