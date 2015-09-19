package com.lob.mwhd.parse;

import android.content.Context;
import android.content.Intent;

import com.lob.mwhd.activities.MainActivity;
import com.parse.ParsePushBroadcastReceiver;

class ParseNotificationReceiver extends ParsePushBroadcastReceiver {

    @Override
    public void onPushOpen(Context context, Intent intent) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}