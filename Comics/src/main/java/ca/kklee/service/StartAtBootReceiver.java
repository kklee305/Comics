package ca.kklee.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.kklee.service.NotificationService;

/**
 * Created by Keith on 30/05/2014.
 */
public class StartAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, NotificationService.class);
            context.startService(serviceIntent);
        }
    }

}
