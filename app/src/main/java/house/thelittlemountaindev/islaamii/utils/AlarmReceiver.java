package house.thelittlemountaindev.islaamii.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import house.thelittlemountaindev.islaamii.AlarmActivity;

/**
 * Created by Charlie One on 12/2/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent();
        i.setClass(context, AlarmActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
