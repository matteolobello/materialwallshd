package com.lob.mwhd.gridview;

/**
 * *******************************************
 * Use a custom adapter for the GridView
 * ********************************************
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lob.mwhd.R;
import com.lob.mwhd.URLs;
import com.lob.mwhd.activities.ApplyWallpaperActivity;
import com.lob.mwhd.helpers.Utils;
import com.lob.mwhd.views.SquaredImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static com.lob.mwhd.URLs.FLAT_PATH;
import static com.lob.mwhd.URLs.GNOW_PATH;
import static com.lob.mwhd.URLs.MATERIAL_PATH;
import static com.lob.mwhd.URLs.MINIMAL_PATH;
import static com.lob.mwhd.URLs.PHOTOGRAPHY_PATH;
import static com.lob.mwhd.URLs.POLY_PATH;
import static com.lob.mwhd.URLs.USERS_PATH;

public class CustomGridViewAdapter extends BaseAdapter {

    private final Activity activity;
    private final String transitionName = "material_transition";

    private ArrayList<String> urls = new ArrayList<>();

    public CustomGridViewAdapter(Activity activity, View rootView, String path) {
        this.activity = activity;

        URLs urlsClass = new URLs();

        if (path.equals(MATERIAL_PATH))
            urls = urlsClass.getMaterialFiles(rootView, activity);
        else if (path.equals(MINIMAL_PATH))
            urls = urlsClass.getMinimalFiles(rootView, activity);
        else if (path.contentEquals(FLAT_PATH))
            urls = urlsClass.getFlatFiles(rootView, activity);
        else if (path.equals(GNOW_PATH))
            urls = urlsClass.getGnowFiles(rootView, activity);
        else if (path.equals(POLY_PATH))
            urls = urlsClass.getPolyFiles(rootView, activity);
        else if (path.equals(PHOTOGRAPHY_PATH))
            urls = urlsClass.getPhotographyFiles(rootView, activity);
        else if (path.equals(USERS_PATH))
            urls = urlsClass.getUserFiles(rootView, activity);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(activity);
            view.setScaleType(CENTER_CROP);
            view.setBackground(activity.getResources().getDrawable(R.drawable.shadow));
            if (Utils.isOnLollipopOrEarlier)
                view.setTransitionName(transitionName);
        }

        try {
            final String url = urls.get(position);

            Picasso.with(activity)
                    .load(url)
                    .placeholder(R.drawable.progress_anim)
                    .error(android.R.drawable.stat_notify_error)
                    .fit()
                    .tag(activity)
                    .into(view);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor = Utils.getSharedPreferences(activity).edit();
                    editor.putString("url", url).apply();

                    Utils.Debug.log(url + " CLICKED");

                    Intent intent = new Intent(activity, ApplyWallpaperActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                                    view,
                                    transitionName
                            );
                    ActivityCompat.startActivity(activity, intent, options.toBundle());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public String getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}