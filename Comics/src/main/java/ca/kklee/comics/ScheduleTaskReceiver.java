package ca.kklee.comics;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import ca.kklee.comics.comic.AbstractComicLoaderFactory;
import ca.kklee.comics.comic.Comic;
import ca.kklee.comics.comic.ComicCollection;
import ca.kklee.util.Logger;

/**
 * Created by Keith on 28/06/2014.
 */
public class ScheduleTaskReceiver extends BroadcastReceiver {

    public static void startScheduledTask(Context context) {
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        if (isAlarmSet(context)) {
            Logger.d("", "Alarm already set");
            return;
        }
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_HOUR * 3, alarmIntent);
        Toast.makeText(context, "Scheduled Download Started", Toast.LENGTH_LONG).show();
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

    @Override
    public void onReceive(Context context, Intent intent) {
        int newComics = downloadFiles(context);
        fireNotification(context, newComics);
    }

    private int downloadFiles(Context context) {
        Logger.d("", "SilentDownload");
        int newComics = 0;
        Comic[] comics = ComicCollection.getInstance().getComics();
        if (comics == null) {
            ComicCollection.getInstance().setComics(context);
            comics = ComicCollection.getInstance().getComics();
        }
        ComicCollection.getInstance().clearAllBitmap();
        for (int i = 0; i < comics.length; i++) {
            Bitmap bitmap = ComicCollection.getInstance().getComics()[i].getBitmap();
            if (bitmap == null) {
                AbstractComicLoaderFactory.getLoader(null, i).execute(comics[i].getUrl());
                newComics++;
            }
        }
        return newComics;
    }

    private void fireNotification(Context context, int newComics) {
        if (newComics == 0) return;
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(newComics + " New Comics Downloaded!")
                        .setContentText(currentDateTimeString)
                        .setContentIntent(startAppIntent(context))
                        .setAutoCancel(true);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private PendingIntent startAppIntent(Context context) {
        Intent i = new Intent(context, HomeActivity.class);
        return PendingIntent.getActivity(context, 0, i, 0);
    }

}
