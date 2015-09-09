package com.lob.mwhd.activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lob.mwhd.R;
import com.lob.mwhd.helpers.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.view.ViewAnimationUtils.createCircularReveal;

public class ApplyWallpaperActivity extends ActionBarActivity {

    private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
    private String url;
    private FloatingActionButton floatingActionButton;
    private View actionBar;
    private Activity currentActivity;
    private int lightColor, darkColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_wallpaper);

        currentActivity = this;

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);

        Utils.Debug.log(getClass().getSimpleName() + " has been loaded");

        TextView title = (TextView) findViewById(R.id.ab_title);
        actionBar = findViewById(R.id.custom_action_bar);
        actionBar.setVisibility(View.GONE);

        if (Utils.isOnline(getApplicationContext())) {
            Utils.Debug.log("Device is connected to Internet");
            url = Utils.getSharedPreferences(getApplicationContext()).getString("url", null);

            title.setText(Utils.getTitleForActionBarFromURL(url));

            final ImageView imageView = (ImageView) findViewById(R.id.big_image_preview);

            Picasso.with(getApplicationContext())
                    .load(url)
                    .placeholder(R.drawable.progress_anim)
                    .error(android.R.drawable.stat_notify_error)
                    .fit()
                    .tag(getApplicationContext())
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Palette palette = Palette.from(((BitmapDrawable) imageView.getDrawable()).getBitmap()).generate();

                            lightColor = palette.getVibrantColor(palette.getMutedColor(getResources().getColor(R.color.ColorPrimary)));
                            darkColor = palette.getDarkVibrantColor(palette.getDarkMutedColor(getResources().getColor(R.color.ColorPrimaryDark)));
                            setupUI(lightColor, darkColor);

                            Utils.setupMutitaskingBarOnLollipop(currentActivity,
                                    palette.getVibrantColor(palette.getMutedColor(getResources().getColor(R.color.ColorPrimary))));
                        }

                        @Override
                        public void onError() {
                        }
                    });
        } else {
            Utils.Debug.log("Device is NOT connected to Internet");
            Utils.showAlertDialogNotOnline(this);
        }
    }

    private void setupUI(final int colorLight, final int colorDark) {
        actionBar.setVisibility(View.VISIBLE);
        actionBar.setBackgroundColor(colorLight);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(colorDark);
            getWindow().setNavigationBarColor(colorDark);
        }
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(colorLight));
        floatingActionButton.setRippleColor(colorDark);
    }

    public void backArrowClick(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        floatingActionButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out));
        super.onBackPressed();
    }

    public void showCredits(View view) {
        Snackbar.make(findViewById(R.id.fab), getString(R.string.credits) + ": " + Utils.getAuthorFromURL(url).replace("-", ""), Snackbar.LENGTH_SHORT)
                .show();
    }

    public void downloadItemClick(View view) {

        File file = new File("/sdcard/MaterialWallsHD");
        if (!file.isDirectory() || !file.exists()) {
            file.mkdir();
        }

        Toast.makeText(getApplicationContext(), getString(R.string.download_started), Toast.LENGTH_SHORT).show();

        String url = Utils.getSharedPreferences(getApplicationContext()).getString("url", null);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(getString(R.string.downloading_wallpaper));
        request.setTitle(getString(R.string.app_name));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(getString(R.string.app_name), Utils.getTitleFromURL(url));

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public void fabClick(final View view) throws IOException {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        InputStream inputStream = new URL(url).openStream();
        wallpaperManager.setStream(inputStream);

        if (Utils.isOnLollipopOrEarlier) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    floatingActionButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out));

                    final View viewRoot = findViewById(R.id.layout_invisible);
                    viewRoot.setBackgroundColor(lightColor);

                    int cx = (floatingActionButton.getLeft() + floatingActionButton.getRight()) / 2;
                    int cy = (floatingActionButton.getTop() + floatingActionButton.getBottom()) / 2;
                    int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

                    Animator anim = createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
                    viewRoot.setVisibility(View.VISIBLE);
                    anim.setDuration(350);
                    anim.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            Runnable task = new Runnable() {
                                public void run() {
                                    finish();
                                    startActivity(Utils.homeIntent());
                                }
                            };
                            worker.schedule(task, 1, TimeUnit.SECONDS);
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {
                        }
                    });
                    anim.start();
                }
            });
        } else {
            Snackbar.make(findViewById(R.id.layout_apply_wallpaper), getString(R.string.done), Snackbar.LENGTH_SHORT).show();
            startActivity(Utils.homeIntent());
        }
    }
}
