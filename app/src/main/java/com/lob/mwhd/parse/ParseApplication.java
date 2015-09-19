package com.lob.mwhd.parse;

import android.app.Application;
import android.content.Context;

import com.lob.mwhd.activities.MainActivity;
import com.parse.Parse;
import com.parse.ParseConfig;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.PushService;

public class ParseApplication extends Application {
    private static ParseApplication instance = new ParseApplication();

    public ParseApplication() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, ParseConstants.APP_KEY, ParseConstants.CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
    }
}