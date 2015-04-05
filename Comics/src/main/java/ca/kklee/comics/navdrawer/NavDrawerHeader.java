package ca.kklee.comics.navdrawer;

import android.content.SharedPreferences;
import android.widget.TextView;
import ca.kklee.comics.SharedPrefConstants;

/**
 * Created by Keith on 03/04/2015.
 */
public class NavDrawerHeader {

    public static void update(SharedPreferences prefForNew, TextView lastUpdate) {
        long updateTime = prefForNew.getLong(SharedPrefConstants.LASTUPDATE, 0);
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - updateTime;
        String text = "--";
        if (updateTime != 0) {
            if (timeDiff < 1000 * 60) {
                text = (int) (timeDiff / 1000) + "s";
            } else if (timeDiff < 1000 * 60 * 60) {
                text = (int) (timeDiff / 60000) + "m";
            } else if (timeDiff < 1000 * 60 * 60 * 24) {
                text = (int) (timeDiff / 3600000) + "hr";
            } else {
                text = (int) (timeDiff / 86400000) + "d";
            }
        }
        lastUpdate.setText("Last Update: " + text);
    }

}
