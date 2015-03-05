package ca.kklee.comics.scheduletask;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.kklee.utilities.ConnectionUtil;
import com.kklee.utilities.Logger;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import ca.kklee.comics.HomeActivity;
import ca.kklee.comics.R;
import ca.kklee.comics.SharedPrefConstants;
import ca.kklee.comics.comic.Comic;
import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.comics.comic.ComicLoader;

/**
 * Created by Keith on 28/06/2014.
 */
public class ScheduleTaskReceiver extends BroadcastReceiver {

    private static final long INTERVAL_MILLIS = AlarmManager.INTERVAL_HOUR * 6;
    private static int newComics = 0;
    private static int dlComplete = 0;
    private final int NOTIFICATION_ID = 1;

    public static void startScheduledTask(Context context) {
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        if (isAlarmSet(context)) {
            Logger.d("", "Alarm already set");
            return;
        }
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, nearestQuarter(calendar));
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL_MILLIS, alarmIntent);
        Toast.makeText(context, "Scheduled Download Started", Toast.LENGTH_LONG).show();
    }

    public static void startDebugging(Context context) {
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000 * 20, alarmIntent);
        SharedPreferences preferences = context.getSharedPreferences("debuggingAlarm", 0);
        SharedPreferences.Editor editor2 = preferences.edit();
        editor2.putBoolean("alarmOff", true);
        editor2.apply();
    }

    public static boolean isAlarmSet(Context context) {
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    public static void cancelAlarm(Context context) {
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.cancel(alarmIntent);
        alarmIntent.cancel();
        Toast.makeText(context, "Scheduled Download Cancelled", Toast.LENGTH_LONG).show();
    }

    private static int nearestQuarter(Calendar calendar) {
        switch (calendar.get(Calendar.HOUR_OF_DAY)) {
            case 0:
            case 1:
            case 2:
//                return 3;
            case 3:
            case 4:
            case 5:
                return 6;
            case 6:
            case 7:
            case 8:
//                return 9;
            default:
                return 0;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!checkConnection(context)) return;
        initComics(context);
        downloadFiles(context);
        debugging(context);
    }

    private boolean checkConnection(Context context) {
        if (!ConnectionUtil.isOnline(context)) {
            Logger.d("", "No Connection, Silent Download delayed");
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

    private void initComics(Context context) {
        if (ComicCollection.getInstance().getComics() == null) {
            ComicCollection.getInstance().setComics(context);
        }
    }

    private void downloadFiles(final Context context) {
        Logger.d("", "SilentDownload");

        final SharedPreferences prefForNew = context.getSharedPreferences(SharedPrefConstants.COMICNEWFLAG, 0);
        final SharedPreferences.Editor editorForNew = prefForNew.edit();
        final SharedPreferences prefForTime = context.getSharedPreferences(SharedPrefConstants.COMICUPDATETIME, 0);
        final SharedPreferences.Editor editorForTime = prefForTime.edit();

        final Comic[] comics = ComicCollection.getInstance().getComics();
        newComics = 0;
        dlComplete = 0;
        final NewComicListener newComicListener = new NewComicListener() {
            @Override
            public void onDomCheckCompleted(int response, String title) {
                dlComplete++;
                if (response == 1) {
                    editorForNew.putBoolean(title, true);
                    editorForNew.commit();
                    editorForTime.putLong(title, System.currentTimeMillis());
                    editorForTime.apply();
                }
                if (prefForNew.getBoolean(title, false)) {
                    newComics++;
                }
                if (dlComplete == comics.length) {
                    Logger.d("", "Done All Scheduled DL, new comics: " + newComics);
                    if (newComics == 0) return;
                    editorForNew.putBoolean(SharedPrefConstants.OPENDRAWER, true);
                    editorForNew.apply();
                    fireNotification(context, newComics);
                }
            }
        };

//        ComicCollection.getInstance().clearAllBitmap();
        for (int i = 0; i < comics.length; i++) {
            new ComicLoader(null, i, newComicListener).execute(comics[i].getUrl());
        }
    }

    private void fireNotification(Context context, int newComics) {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(newComics + " New Comics Downloaded!")
                        .setContentText(currentDateTimeString)
                        .setContentIntent(startAppIntent(context))
                        .setAutoCancel(true);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private PendingIntent startAppIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        return PendingIntent.getActivity(context, 0, i, 0);
    }

    private void debugging(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("debuggingAlarm", 0);
        SharedPreferences.Editor editor2 = preferences.edit();
        if (preferences.getBoolean("alarmOff", false)) {
            cancelAlarm(context);
            editor2.remove("alarmOff");
            editor2.apply();
        }
    }
}
