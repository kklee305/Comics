package ca.kklee.comics.scheduletask;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.kklee.utilities.Logger;

/**
 * Created by Keith on 27/06/2015.
 */
public class OnBootCompletedReceiver extends BroadcastReceiver {

    public static void registerMe(Context context) {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, OnBootCompletedReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Logger.i("OnBootReceiver enabled");
    }

    public static void unregisterMe(Context context) {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context, OnBootCompletedReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Logger.i("OnBootReceiver disabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ScheduleTaskReceiver.startScheduledTask(context);
    }

}
