package ca.kklee.comics;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import ca.kklee.util.Logger;

/**
 * TODO List
 * Alarm switch in options
 *
 */

public class HomeActivity extends ActionBarActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_home);

        initComicCollection();

        ScheduleTaskReceiver.startScheduledTask(this);

        initOptions();

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    private void initComicCollection() {
        if (ComicCollection.getInstance().getComics() == null) {
            ComicCollection.getInstance().setComics(this);
        }
    }

    private void initOptions() {
        ImageView view = (ImageView) findViewById(R.id.swipe_options);
        final Activity activity = this;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Logger.d("", "options");
                Dialog dialog = OptionsDialogFactory.createDialog(activity);
                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();
                wlp.gravity = Gravity.BOTTOM | Gravity.LEFT;
                window.setAttributes(wlp);
                dialog.show();
            }
        });
    }

}
