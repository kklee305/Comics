package ca.kklee.comics.viewpager;

import android.app.Activity;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import ca.kklee.util.Logger;

/**
 * Created by Keith on 07/07/2014.
 */
public class GestureListener extends SimpleOnGestureListener {

    private View decorView;

    public GestureListener(Activity activity) {
        decorView = activity.getWindow().getDecorView();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Logger.d("", "SingleTapUp");
        if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            hideUI();
        } else {
            showUI();
        }
        return true;
    }

    private void hideUI() {
        Logger.d("", "hide UI");
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    private void showUI() {

        Logger.d("", "show UI");
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }
}
