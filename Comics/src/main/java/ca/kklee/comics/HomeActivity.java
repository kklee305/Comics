package ca.kklee.comics;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.comics.navdrawer.DrawerItemClickListener;
import ca.kklee.comics.navdrawer.NavDrawerAdapter;
import ca.kklee.comics.viewpager.SectionsPagerAdapter;

/**
 * TODO List
 * logger
 * custom options menu
 * display error icons ??
 * proper image scaling
 * image pinch zooming
 * limit async task (add resource pool)
 * view comics of diff dates ***
 * add authors
 */

public class HomeActivity extends ActionBarActivity {

    DrawerLayout drawerLayout;
    private ViewPager viewPager;
    private ActionBarDrawerToggle drawerToggle;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ListView drawerList;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            hideUI(getWindow().getDecorView());
        }
    };

    public static void hideUI(View decorView) {
        if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );
        }
    }

    public static void showUI(View decorView) {
        if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_FULLSCREEN) != 0) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        pref = getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0);
        editor = pref.edit();
        hideUI(getWindow().getDecorView());
        initComicCollection(); //do this before everything else
        initComicPager();
        initNavDrawer(); //ComicPager comes before this
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
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 0, 0) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                hideUI(getWindow().getDecorView());
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerList.invalidateViews();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerList.setAdapter(new NavDrawerAdapter(this, R.layout.nav_list_item_layout, ComicCollection.getInstance().getFullTitleArray()));
        drawerList.setOnItemClickListener(new DrawerItemClickListener(viewPager, drawerList, drawerLayout));

        View header = getLayoutInflater().inflate(R.layout.nav_list_header_layout, null);
        drawerList.addHeaderView(header);
        drawerList.setHeaderDividersEnabled(false);
        drawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        drawerList.setItemChecked(1, true);

        ImageView navButton = (ImageView) findViewById(R.id.nav_icon);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerList);
            }
        });
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
                String title = ComicCollection.getInstance().getComics()[position].getTitle();
                if (pref.getBoolean(title, false)) {
                    editor.putBoolean(title, false);
                    editor.commit();
                }
                drawerList.setItemChecked(position+1, true); //+1 due to header
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

        PagerTitleStrip pagerTitleStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/ComicNeue-Regular-Oblique.ttf");
        for (int counter = 0; counter < pagerTitleStrip.getChildCount(); counter++) {

            if (pagerTitleStrip.getChildAt(counter) instanceof TextView) {
                ((TextView) pagerTitleStrip.getChildAt(counter)).setTypeface(typeface);
                ((TextView) pagerTitleStrip.getChildAt(counter)).setTextSize(18);
            }
        }
    }

    private void initOptions() {
        ImageView optionsButton = (ImageView) findViewById(R.id.options_icon);
        final Activity activity = this;
        optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = OptionsDialogFactory.createDialog(activity);
                dialog.show();
            }
        });
        optionsButton.setVisibility(View.VISIBLE);
    }

    private void initImmersionFullScreen() {
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                // Note that system bars will only be "visible" if none of the
                // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    // The system bars are visible
                    handler.postDelayed(runnable, 1000 * 5);
                } else {
                    // The system bars are NOT visible
                    stopAutoHideUI();
                }
            }
        });
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        drawerToggle.syncState();
//    }

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
                hideUI(getWindow().getDecorView());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pref.getBoolean(SharedPrefConstants.OPENDRAWER, true)) {
            drawerLayout.openDrawer(drawerList);
            editor.putBoolean(SharedPrefConstants.OPENDRAWER, false);
            editor.commit();
            ComicCollection.getInstance().clearAllBitmap();
        }
    }

    private void stopAutoHideUI() {
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

//        switch (item.getItemId()) {
//            case R.id.action_clear:
//                BitmapLoader.clearBitmap();
//                  Intent intent = new Intent(this, HomeActivity.class);
//                  finish();
//                  startActivity(intent);
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
//            case 3:
//                debugging(activity);
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }
}
