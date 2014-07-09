package ca.kklee.comics;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.comics.navdrawer.DrawerItemClickListener;
import ca.kklee.comics.viewpager.SectionsPagerAdapter;
import ca.kklee.util.Logger;

/**
 * TODO List
 * logger
 * add other comics
 * proper image scaling
 * limit async task (add resource pool)
 * view comics of diff dates ***
 * clean up comic object
 * add authors
 * GestureListener for Immersive
 */

public class HomeActivity extends ActionBarActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            hideUI();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        initComicCollection(); //do this before everything else
        initComicPager();
        initNavDrawer(); //ComicPager comes first

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            initOptions();
        } else {
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
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 0, 0) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                hideUI();
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ComicCollection.getInstance().getTitleArray()));
        drawerList.setOnItemClickListener(new DrawerItemClickListener(viewPager, drawerLayout));
    }

    private void initComicPager() {
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
    }

    private void initOptions() {
        ImageView view = (ImageView) findViewById(R.id.options);
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
                hideUI();
            }
        }
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
                }
            }
        });
    }

    public void hideUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        stopAutoHideUI();
        menu.findItem(R.id.action_schedule_switch).setTitle(getAlarmStateString());
        return super.onPrepareOptionsMenu(menu);
    }

    private String getAlarmStateString() {
        if (ScheduleTaskReceiver.isAlarmSet(this)) {
            return "Set Auto-DL OFF";
        } else {
            return "Set Auto-DL ON";
        }
    }

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

        switch (item.getItemId()) {
            case R.id.action_clear:
                BitmapLoader.clearBitmap();
//                Intent intent = new Intent(this, HomeActivity.class);
//                finish();
//                startActivity(intent);
                return true;
            case R.id.action_schedule_switch:
                if (ScheduleTaskReceiver.isAlarmSet(this)) {
                    ScheduleTaskReceiver.cancelAlarm(this);
                } else {
                    ScheduleTaskReceiver.startScheduledTask(this);
                }
                return true;
            case R.id.action_about:
                Toast.makeText(this, "Keith made this", Toast.LENGTH_SHORT).show();
                return true;
//                            case 3:
//                                debugging(activity);
//                                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void stopAutoHideUI() {
        Logger.d("Immersion", "Stop Timer");
        handler.removeCallbacks(runnable);
    }
}
