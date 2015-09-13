package com.lob.mwhd.helpers;

/**
 * ***************************************************
 * The "Utils" class where I can organize my methods
 * ***************************************************
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.lob.mwhd.R;
import com.lob.mwhd.URLs;

import static com.lob.mwhd.helpers.Utils.Debug.log;

public class Utils {

    public static boolean isOnLollipopOrEarlier = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

    public static void setFragment(FragmentManager fragmentManager, Fragment fragment) {
        try {
            log("Loading " + fragment.getClass().getSimpleName());
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
            fragmentTransaction.commit();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void setupAds(View rootView) {
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);

        if (getSharedPreferences(rootView.getContext()).getBoolean("mustShowAds", true)) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
        } else {
            mAdView.setVisibility(View.GONE);
        }
    }

    public static void setStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                setupMutitaskingBarOnLollipop(activity, 0);
        }
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setupMutitaskingBarOnLollipop(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            activity.setTaskDescription(
                    new ActivityManager.TaskDescription(
                            "MaterialWallsHD", Utils.drawableToBitmap(activity.getDrawable(R.drawable.m)),
                            color != 0 ? color : activity.getResources().getColor(R.color.ColorPrimary)));
    }

    public static String getAuthorFromURL(String url) {
        String tmp = getTitleForActionBarFromURL(url);
        return tmp.substring(tmp.lastIndexOf(" - ") + 1);
    }

    public static String getTitleForActionBarFromURL(String url) {
        return url.replace(URLs.MAIN_URL, "")
                .replace(URLs.MATERIAL_PATH, "")
                .replace(URLs.MINIMAL_PATH, "")
                .replace(URLs.GNOW_PATH, "")
                .replace(URLs.PHOTOGRAPHY_PATH, "")
                .replace(URLs.POLY_PATH, "")
                .replace(URLs.FLAT_PATH, "")
                .replace(URLs.USERS_PATH, "")
                .replace("/", "")
                .replace(".jpg", "")
                .replace(".jpeg", "")
                .replace(".png", "")
                .replace(".bmp", "")
                .replace("_", " ")
                .replace("-", " - ");
    }

    public static String getTitleFromURL(String url) {
        return url.replace(URLs.MAIN_URL, "")
                .replace(URLs.MATERIAL_PATH, "")
                .replace(URLs.MINIMAL_PATH, "")
                .replace(URLs.GNOW_PATH, "")
                .replace(URLs.PHOTOGRAPHY_PATH, "")
                .replace(URLs.POLY_PATH, "")
                .replace(URLs.FLAT_PATH, "")
                .replace(URLs.USERS_PATH, "")
                .replace("/", "");
    }

    public static Intent homeIntent() {
        return new Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public static void restart(Activity activity, Context context) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        context.startActivity(context.getPackageManager()
                        .getLaunchIntentForPackage(context.getPackageName())
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        );
    }

    public static void showAlertDialogNotOnline(final Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(activity.getString(R.string.error));
        alert.setMessage(activity.getString(R.string.device_is_not_connected));
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static class Debug {

        // Change this boolean to true if you want some logs
        public static final boolean isDebug = false;

        public static void log(String log) {
            if (isDebug)
                Log.d("MaterialWallsHD", log);
        }
    }
}
