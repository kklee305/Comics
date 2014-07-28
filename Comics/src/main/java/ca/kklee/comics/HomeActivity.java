package ca.kklee.comics;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.comics.navdrawer.DrawerItemClickListener;
import ca.kklee.comics.navdrawer.NavDrawerAdapter;
import ca.kklee.comics.viewpager.SectionsPagerAdapter;
import ca.kklee.util.Logger;

/**
 * TODO List
 * updating notifications
 * use actionbar for comic title so to show nav drawer icon
 * custom options menu
 * show which comic is new ** needs custom nav drawer list
 * display error icons ??
 * logger
 * proper image scaling
 * image pinch zooming
 * limit async task (add resource pool)
 * view comics of diff dates ***
 * add authors
 */

public class HomeActivity extends ActionBarActivity {

    private ViewPager viewPager;
    private ActionBarDrawerToggle drawerToggle;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            hideUI(getWindow().getDecorView());
        }
    };

    public static void hideUI(View decorView) {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    public static void showUI(View decorView) {
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Logger.d("", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString());
        Logger.d("", getExternalFilesDir(null).toString());
        Logger.d("", Environment.getDataDirectory().toString());
        Logger.d("", Environment.getExternalStorageDirectory().toString());
        Logger.d("", Environment.getRootDirectory().toString());

        initComicCollection(); //do this before everything else
        initComicPager();
        initNavDrawer(); //ComicPager comes first
        initOptions();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            initImmersionFullScreen();
        }
    }

    private void initComicCollection() {
        if (ComicCollection.getInstance().getComics() == null) {
            ComicCollection.getInstance().setComics(this);
        }
    }

    private void initNavDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ListView drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 0, 0) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                hideUI(getWindow().getDecorView());
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerList.setAdapter(new NavDrawerAdapter(this, R.layout.nav_list_item_layout, ComicCollection.getInstance().getTitleArray()));
        drawerList.setOnItemClickListener(new DrawerItemClickListener(viewPager, drawerLayout));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initComicPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideUI(getWindow().getDecorView());
            }

            @Override
            public void onPageSelected(int position) {
                hideUI(getWindow().getDecorView());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                hideUI(getWindow().getDecorView());
            }
        });
    }

    private void initOptions() {
        ImageView view = (ImageView) findViewById(R.id.options_icon);
        final Activity activity = this;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = OptionsDialogFactory.createDialog(activity);
                dialog.show();
            }
        });
        view.setVisibility(View.VISIBLE);
    }

    private void initImmersionFullScreen() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                // Note that system bars will only be "visible" if none of the
                // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    // TODO: The system bars are visible. Make any desired
                    // adjustments to your UI, such as showing the action bar or
                    // other navigational controls.
                    Logger.d("Immersion", "start timer");
                    handler.postDelayed(runnable, 1000 * 5);
                } else {
                    // TODO: The system bars are NOT visible. Make any desired
                    // adjustments to your UI, such as hiding the action bar or
                    // other navigational controls.
                    stopAutoHideUI();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            if (hasFocus) {
                Logger.d("Immersion", "Hide UI onFocusChange");
                hideUI(getWindow().getDecorView());
            }
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        ComicCollection.getInstance().clearAllBitmap();
//    }

    private void stopAutoHideUI() {
        Logger.d("Immersion", "Stop Timer");
        handler.removeCallbacks(runnable);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        stopAutoHideUI();
//        menu.findItem(R.id.action_schedule_switch).setTitle(getAlarmStateString());
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    private String getAlarmStateString() {
//        if (ScheduleTaskReceiver.isAlarmSet(this)) {
//            return "Set Auto-DL OFF";
//        } else {
//            return "Set Auto-DL ON";
//        }
//    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

//        switch (item.getItemId()) {
//            case R.id.action_clear:
//                BitmapLoader.clearBitmap();
////                Intent intent = new Intent(this, HomeActivity.class);
////                finish();
////                startActivity(intent);
//                return true;
//            case R.id.action_schedule_switch:
//                if (ScheduleTaskReceiver.isAlarmSet(this)) {
//                    ScheduleTaskReceiver.cancelAlarm(this);
//                } else {
//                    ScheduleTaskReceiver.startScheduledTask(this);
//                }
//                return true;
//            case R.id.action_about:
//                Toast.makeText(this, "Keith made this", Toast.LENGTH_SHORT).show();
//                return true;
////                            case 3:
////                                debugging(activity);
////                                break;
//        }
        return super.onOptionsItemSelected(item);
    }
}
