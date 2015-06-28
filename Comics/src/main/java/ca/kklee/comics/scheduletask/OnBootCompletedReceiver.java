package ca.kklee.comics.scheduletask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.kklee.utilities.Logger;

import ca.kklee.comics.SharedPrefConstants;

/**
 * Created by Keith on 27/06/2015.
 */
public class OnBootCompletedReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.setIsLogging(true);
        Logger.setLogToFile(context);
        SharedPreferences pref = context.getSharedPreferences(SharedPrefConstants.ISREFRESHENABLED, Context.MODE_PRIVATE);
        if (pref.getBoolean(SharedPrefConstants.ISREFRESHENABLED, false) && !ScheduleTaskReceiver.isAlarmSet(context)) {
            ScheduleTaskReceiver.startScheduledTask(context);
        }

    }
}
