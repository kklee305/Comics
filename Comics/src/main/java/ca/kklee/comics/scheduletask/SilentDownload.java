package ca.kklee.comics.scheduletask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.kklee.utilities.ConnectionUtil;
import com.kklee.utilities.Logger;

import java.text.DateFormat;
import java.util.Date;

import ca.kklee.comics.HomeActivity;
import ca.kklee.comics.R;
import ca.kklee.comics.SharedPrefConstants;
import ca.kklee.comics.comic.Comic;
import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.comics.comic.ComicLoader;
import ca.kklee.comics.navdrawer.RefreshListener;

/**
 * Created by Keith on 26/03/2015.
 */
public class SilentDownload {

    private enum State {ACTIVE, IDLE}

    private static int newComics = 0;
    private static int dlComplete = 0;
    private final int NOTIFICATION_ID = 1;
    private static State currentState = State.IDLE;
    private RefreshListener refreshListener;
    private Context context;

    public SilentDownload(Context context, RefreshListener refreshListener) {
        this.context = context;
        this.refreshListener = refreshListener;
    }

    public void startSilentDownload() {
        if (currentState == State.ACTIVE) return;
        Logger.setIsLogging(true);
        Logger.setLogToFile(context);
        if (!checkConnection()) return;
        currentState = State.ACTIVE;
        initComics();
        downloadFiles();
//        debugging();
    }

    private boolean checkConnection() {
        if (!ConnectionUtil.isOnline(context)) {
            Logger.w("No Connection, Silent Download delayed");
            Toast.makeText(context,"No Connection Found!", Toast.LENGTH_LONG).show();
            if (refreshListener != null) {
                refreshListener.onRefreshComplete();
            }
            SharedPreferences prefForNew = context.getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0);
            SharedPreferences.Editor editorForNew = prefForNew.edit();
            if (prefForNew.getBoolean(SharedPrefConstants.WIFIRECONNECT, false)) {
                return false;
            }
//            OnWifiConnectedReceiver broadcastReceiver = new OnWifiConnectedReceiver();
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//            context.getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);

            editorForNew.putBoolean(SharedPrefConstants.WIFIRECONNECT, true);
            editorForNew.apply();
            return false;
        }
        return true;
    }

    private void initComics() {
        if (ComicCollection.getInstance().getComics() == null) {
            ComicCollection.getInstance().setComics(context);
        }
    }

    private void downloadFiles() {
        Logger.i("SilentDownload starting");
        final SharedPreferences prefForNew = context.getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0);
        final SharedPreferences.Editor editorForNew = prefForNew.edit();
        final SharedPreferences prefForError = context.getSharedPreferences(SharedPrefConstants.COMICERRORFLAG, 0);
        final SharedPreferences.Editor editorForError = prefForError.edit();
        final SharedPreferences prefForTime = context.getSharedPreferences(SharedPrefConstants.COMICUPDATETIME, 0);
        final SharedPreferences.Editor editorForTime = prefForTime.edit();

        final Comic[] comics = ComicCollection.getInstance().getComics();
        newComics = 0;
        dlComplete = 0;
        final NewComicListener newComicListener = new NewComicListener() {
            @Override
            public void onDomCheckCompleted(ResponseCode response, String title) {
                dlComplete++;
                switch (response) {
                    case UPDATED:
                        editorForNew.putBoolean(title, true);
                        editorForNew.commit();
                        editorForTime.putLong(title, System.currentTimeMillis());
                        editorForTime.apply();
                    case NOUPDATE:
                        editorForError.putBoolean(title, false);
                        break;
                    case ERROR:
                        editorForError.putBoolean(title, true);
                        break;
                }
                editorForError.apply();

                if (prefForNew.getBoolean(title, false)) {
                    newComics++;
                }
                if (dlComplete >= comics.length) {
                    Logger.i("Done All Scheduled DL, new comics: " + newComics);
                    editorForNew.putLong(SharedPrefConstants.LASTUPDATE, System.currentTimeMillis());
                    editorForNew.commit();
                    if (refreshListener != null) {
                        refreshListener.onRefreshComplete();
                    }
                    currentState = State.IDLE;
                    if (newComics == 0) return;
                    editorForNew.putBoolean(SharedPrefConstants.OPENDRAWER, true);
                    editorForNew.commit();
                    fireNotification(newComics);
                }
            }
        };

        for (int i = 0; i < comics.length; i++) {
            new ComicLoader(null, i, newComicListener).execute(comics[i].getUrl());
        }
    }

    private void fireNotification(int newComics) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(newComics + " New Comics Downloaded!")
                        .setContentText(currentDateTimeString)
                        .setContentIntent(startAppIntent())
                        .setAutoCancel(true);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private PendingIntent startAppIntent() {
        Intent i = new Intent(context, HomeActivity.class);
        return PendingIntent.getActivity(context, 0, i, 0);
    }

//    private void debugging(Context context) {
//        SharedPreferences preferences = context.getSharedPreferences("debuggingAlarm", 0);
//        SharedPreferences.Editor editor2 = preferences.edit();
//        if (preferences.getBoolean("alarmOff", false)) {
//            cancelAlarm(context);
//            editor2.remove("alarmOff");
//            editor2.apply();
//        }
//    }
}
