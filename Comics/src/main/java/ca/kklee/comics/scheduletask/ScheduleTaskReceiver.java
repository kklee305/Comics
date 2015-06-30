package ca.kklee.comics.scheduletask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.kklee.utilities.Logger;

import java.util.Calendar;

/**
 * Created by Keith on 28/06/2014.
 */
public class ScheduleTaskReceiver extends BroadcastReceiver {

    private static final long INTERVAL_MILLIS = AlarmManager.INTERVAL_HOUR * 6;

    public static void startScheduledTask(Context context) {
        Intent intent = new Intent(context, ScheduleTaskReceiver.class);
        if (isAlarmSet(context)) {
            Logger.i("Alarm already set");
            return;
        }
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, nearestQuarter(calendar));
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarms.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), INTERVAL_MILLIS, alarmIntent);
        OnBootCompletedReceiver.registerMe(context);
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
        OnBootCompletedReceiver.unregisterMe(context);
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
        new SilentDownload(context, null).startSilentDownload();
    }

}
