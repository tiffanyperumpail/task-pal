package com.taskpal.taskpal;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    public void onReceive(Context context, Intent intent) {
        //Trigger the notification
        NotificationScheduler.showNotification(context, NotificationActivity.class,
                "Task Check-in", "Have you completed TASK_NAME_HERE");
    }

    public void onReceive(Intent intent) {

    }
}
