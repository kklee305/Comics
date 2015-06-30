package ca.kklee.comics.options;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ca.kklee.comics.R;
import ca.kklee.comics.SharedPrefConstants;
import ca.kklee.comics.scheduletask.ScheduleTaskReceiver;

/**
 * Created by Keith on 24/06/2015.
 */
public class OptionsActivity extends AppCompatActivity {

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        list = (ListView) findViewById(R.id.options_list);
        ArrayList<String> dummyList = new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dummyList); //:P
        list.setAdapter(adapter);

        initAutoRefreshSwitchView();
    }

    private void initAutoRefreshSwitchView() {
        View refreshSwitchLayout = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.options_switch_layout, null, false);
        SwitchCompat switchCompat = (SwitchCompat) refreshSwitchLayout.findViewById(R.id.options_refresh_switch);
        TextView option_text = (TextView) refreshSwitchLayout.findViewById(R.id.options_text);
        option_text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ComicNeue-Regular-Oblique.ttf"));
        switchCompat.setChecked(ScheduleTaskReceiver.isAlarmSet(this));

        final Context context = this.getApplicationContext();
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!ScheduleTaskReceiver.isAlarmSet(context)) {
                        ScheduleTaskReceiver.startScheduledTask(context);
                        buttonView.setChecked(ScheduleTaskReceiver.isAlarmSet(context));
                    }
                } else {
                    if (ScheduleTaskReceiver.isAlarmSet(context)) {
                        ScheduleTaskReceiver.cancelAlarm(context);
                        buttonView.setChecked(ScheduleTaskReceiver.isAlarmSet(context));
                    }
                }
            }
        });
        list.addFooterView(refreshSwitchLayout);
    }
}
