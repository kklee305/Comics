package ca.kklee.comics;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import ca.kklee.util.AppConfig;

public class HomeActivity extends ActionBarActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initComicCollection();
        dlComicCollection();

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);

    }

    private void dlComicCollection() {

    }

    private void initComicCollection() {
        if (ComicCollection.getInstance().getComics() == null) {
            ComicCollection.getInstance().setComics(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        if (AppConfig.IS_DEBUGGING())
            menu.findItem(R.id.menu_debugging).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_about:
                return true;
            case R.id.menu_debugging:
                debugging();
        }
        return super.onOptionsItemSelected(item);
    }

    private void debugging() {
        Log.d("DEBUGGING", "test notifications");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        Log.d("DEBUGGING", "testing parsing");
        Comic[] comics = ComicCollection.getInstance().getComics();
        Log.d("test parsing", "" + comics.length);
        for (int i = 0; i < comics.length; i++) {
            Log.d("test parsing", comics[i].toString());
        }
    }

}
