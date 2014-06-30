package ca.kklee.comics;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import ca.kklee.util.Logger;

public class HomeActivity extends ActionBarActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initComicCollection();

        ScheduleTaskReceiver.startScheduledTask(this);

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(sectionsPagerAdapter);
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
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_clear:
                clear();
                break;
            case R.id.menu_about:
                break;
            case R.id.menu_debugging:
                debugging();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clear() {
        for(Comic c : ComicCollection.getInstance().getComics()) {
            c.clearBitmap();
        }
        BitmapLoader.clearDir();
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    private void debugging() {
        Logger.d("DEBUGGING", "test notifications");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());

        Logger.d("DEBUGGING", "testing parsing");
        Comic[] comics = ComicCollection.getInstance().getComics();
        Logger.d("test parsing", "Number of comics: " + comics.length);
        for (int i = 0; i < comics.length; i++) {
            Logger.d("test parsing", comics[i].toString());
        }
//        Logger.d("DEBUGGING", "testing image to binary");
//        Bitmap bitmap = ComicCollection.getInstance().getComics()[0].getBitmap();
//        BitmapLoader.saveBitmap("testing2",bitmap);

//        Logger.d("DEBUGGING", "testing bianary to image");
//        Bitmap bitmap = BitmapLoader.loadBitmap("testing");
//        if (bitmap != null) {
//            Logger.d("","Yay worked");
//        }
    }

}
