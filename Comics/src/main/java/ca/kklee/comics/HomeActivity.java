package ca.kklee.comics;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kklee.utilities.Logger;

import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.comics.navdrawer.DrawerItemClickListener;
import ca.kklee.comics.navdrawer.NavDrawerAdapter;
import ca.kklee.comics.navdrawer.NavDrawerHeader;
import ca.kklee.comics.navdrawer.RefreshListener;
import ca.kklee.comics.scheduletask.ScheduleTaskReceiver;
import ca.kklee.comics.scheduletask.SilentDownload;
import ca.kklee.comics.viewpager.SectionsPagerAdapter;

/**
 * TODO List
 * custom options menu
 * proper image scaling
 * image pinch zooming
 * view comics of diff dates
 * add authors
 */

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private LinearLayout drawerLinear;
    private ViewPager viewPager;
    private ActionBarDrawerToggle drawerToggle;
    private SwipeRefreshLayout srl;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
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

        //setLogger
        Logger.setIsLogging(true);
        Logger.setLogToFile(getApplicationContext());

        pref = getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, Context.MODE_PRIVATE);
        editor = pref.edit();

        initComicCollection(); //do this before everything else
        initComicPager();
        initNavDrawer(); //ComicPager comes before this
//        initTestButton();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            hideUI(getWindow().getDecorView());
            initImmersionFullScreen();
        }
    }

    private void initComicCollection() {
        if (ComicCollection.getInstance().getComics() == null) {
            ComicCollection.getInstance().setComics(this);
        }
    }

    private void initComicPager() {
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                hideUI(getWindow().getDecorView());
            }

            @Override
            public void onPageSelected(int position) {
                drawerList.setItemChecked(position, true);
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

    private void initNavDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLinear = (LinearLayout) findViewById(R.id.left_drawer);
        drawerList = (ListView) findViewById(R.id.drawer_list_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                hideUI(getWindow().getDecorView());
                super.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerList.invalidateViews();
                NavDrawerHeader.update(getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0), (TextView) findViewById(R.id.comic_header_last_update));
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                String title = ComicCollection.getInstance().getComics()[viewPager.getCurrentItem()].getTitle();
                if (pref.getBoolean(title, false)) {
                    editor.putBoolean(title, false);
                    editor.commit();
                }
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerList.setAdapter(new NavDrawerAdapter(this, R.layout.nav_list_item_layout, ComicCollection.getInstance().getFullTitleArray()));
        drawerList.setOnItemClickListener(new DrawerItemClickListener(this, viewPager, drawerLayout));

        drawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        drawerList.setItemChecked(0, true);

        drawerList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (i == SCROLL_STATE_IDLE)
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                else
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {

            }
        });

        final RefreshListener refreshListener = new RefreshListener() {
            @Override
            public void onRefreshComplete() {
                srl.setRefreshing(false);
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                viewPager.getAdapter().notifyDataSetChanged();
                NavDrawerHeader.update(getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0), (TextView) findViewById(R.id.comic_header_last_update));
            }
        };
        srl = (SwipeRefreshLayout) findViewById(R.id.drawer_swipe_refresh_view);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(refreshListener);
            }
        });
        srl.setProgressBackgroundColorSchemeResource(R.color.primary_4);
        srl.setColorSchemeResources(R.color.primary_2, R.color.complement_2);
        ImageView navButton = (ImageView) findViewById(R.id.nav_icon);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(drawerLinear);
            }
        });

        NavDrawerHeader.update(getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0), (TextView) findViewById(R.id.comic_header_last_update));

        //move to somewhere else... maybe
        View refreshSwitchLayout = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.options_switch_item_layout, null, false);
        SwitchCompat switchCompat = (SwitchCompat) refreshSwitchLayout.findViewById(R.id.options_refresh_switch);
        TextView option_text = (TextView) refreshSwitchLayout.findViewById(R.id.options_text);
        option_text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ComicNeue-Regular-Oblique.ttf"));
        switchCompat.setChecked(ScheduleTaskReceiver.isAlarmSet(this));
        final HomeActivity homeActivity = this;
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!ScheduleTaskReceiver.isAlarmSet(homeActivity)) {
                        ScheduleTaskReceiver.startScheduledTask(homeActivity);
                        buttonView.setChecked(ScheduleTaskReceiver.isAlarmSet(homeActivity));
                    }
                } else {
                    if (ScheduleTaskReceiver.isAlarmSet(homeActivity)) {
                        ScheduleTaskReceiver.cancelAlarm(homeActivity);
                        buttonView.setChecked(ScheduleTaskReceiver.isAlarmSet(homeActivity));
                    }
                }
            }
        });
        drawerList.addFooterView(refreshSwitchLayout);

        View footer = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.nav_list_footer_layout, null, false);
        drawerList.addFooterView(footer);
    }

    private void initTestButton() {
//        final LinearLayout optionsDrawer = (LinearLayout) findViewById(R.id.options_drawer);

        ImageView testButton = (ImageView) findViewById(R.id.test_icon);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                drawerLayout.openDrawer(optionsDrawer);
            }
        });
        testButton.setVisibility(View.VISIBLE);
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
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (pref.getBoolean(SharedPrefConstants.OPENDRAWER, true)) {
            if (viewPager != null) {
                viewPager.getAdapter().notifyDataSetChanged();
            }
            drawerLayout.openDrawer(drawerLinear);
            editor.putBoolean(SharedPrefConstants.OPENDRAWER, false);
            editor.commit();

            //find first new comic
            String title;
            for (int i = 0; i < ComicCollection.getInstance().getComics().length; i++) {
                title = ComicCollection.getInstance().getComics()[i].getTitle();
                if (pref.getBoolean(title, false)) {
                    drawerList.setItemChecked(i, true);
                    drawerList.smoothScrollToPosition(i);
                    viewPager.setCurrentItem(i, false);
                    break;
                }
            }
        }
    }

    protected void refresh(RefreshListener refreshListener) {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        new SilentDownload(this.getApplicationContext(), refreshListener).startSilentDownload();
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
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
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
//        return super.onOptionsItemSelected(item);
//    }
}
