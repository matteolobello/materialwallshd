package com.lob.mwhd;

/*
 * **********************************************
 * Used by every Fragment to "contact" the server
 * **********************************************
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.lob.mwhd.helpers.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class URLs {

    public static final String MAIN_URL = "http://www.mwhd.altervista.org/";
    public static final String USER_UPLOAD_PHP_SCRIPT = "http://mwhd.altervista.org/php/UploadFileFromApp.php";
    public static final String MATERIAL_PATH = "/wp_upload/wallpapers/material";
    public static final String MINIMAL_PATH = "/wp_upload/wallpapers/minimal";
    public static final String FLAT_PATH = "/wp_upload/wallpapers/flat";
    public static final String GNOW_PATH = "/wp_upload/wallpapers/gnow";
    public static final String POLY_PATH = "/wp_upload/wallpapers/poly";
    public static final String PHOTOGRAPHY_PATH = "/wp_upload/wallpapers/photography";
    public static final String USERS_PATH = "/wp_upload/wallpapers/users";

    public static final String WEBSITE = "http://ohmylob.github.io";

    private static final String INDEX_PHP = "/index.php";

    public static ArrayList<String> allFiles = new ArrayList<>();
    private static ArrayList<String> materialFiles = new ArrayList<>();
    private static ArrayList<String> minimalFiles = new ArrayList<>();
    private static ArrayList<String> flatFiles = new ArrayList<>();
    private static ArrayList<String> gnowFiles = new ArrayList<>();
    private static ArrayList<String> polyFiles = new ArrayList<>();
    private static ArrayList<String> photographyFiles = new ArrayList<>();
    private static ArrayList<String> usersFiles = new ArrayList<>();

    static {
        Utils.Debug.log("Setup StrictMode.ThreadPolicy");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public ArrayList<String> getMaterialFiles(View rootView, Activity activity) {
        Document doc;
        materialFiles.clear();
        try {
            doc = Jsoup.connect(MAIN_URL + MATERIAL_PATH + INDEX_PHP).timeout(10*1000).get();
            new GetLinks(activity,rootView,  doc, MATERIAL_PATH).execute(materialFiles, materialFiles, materialFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return materialFiles;
    }

    public ArrayList<String> getMinimalFiles(View rootView, Activity activity) {
        Document doc;
        minimalFiles.clear();
        try {
            doc = Jsoup.connect(MAIN_URL + MINIMAL_PATH + INDEX_PHP).timeout(10 * 1000).get();
            new GetLinks(activity, rootView, doc, MINIMAL_PATH).execute(minimalFiles, minimalFiles, minimalFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return minimalFiles;
    }

    public ArrayList<String> getFlatFiles(View rootView, Activity activity) {
        Document doc;
        flatFiles.clear();
        try {
            doc = Jsoup.connect(MAIN_URL + FLAT_PATH + INDEX_PHP).timeout(10*1000).get();
            new GetLinks(activity, rootView, doc, FLAT_PATH).execute(flatFiles, flatFiles, flatFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flatFiles;
    }

    public ArrayList<String> getGnowFiles(View rootView, Activity activity) {
        Document doc;
        gnowFiles.clear();
        try {
            doc = Jsoup.connect(MAIN_URL + GNOW_PATH + INDEX_PHP).timeout(10 * 1000).get();
            new GetLinks(activity, rootView, doc, GNOW_PATH).execute(gnowFiles, gnowFiles, gnowFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gnowFiles;
    }

    public ArrayList<String> getPolyFiles(View rootView, Activity activity) {
        Document doc;
        polyFiles.clear();
        try {
            doc = Jsoup.connect(MAIN_URL + POLY_PATH + INDEX_PHP).timeout(10 * 1000).get();
            new GetLinks(activity, rootView, doc, POLY_PATH).execute(polyFiles, polyFiles, polyFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return polyFiles;
    }

    public ArrayList<String> getPhotographyFiles(View rootView, Activity activity) {
        Document doc;
        photographyFiles.clear();
        try {
            doc = Jsoup.connect(MAIN_URL + PHOTOGRAPHY_PATH + INDEX_PHP).timeout(10 * 1000).get();
            new GetLinks(activity, rootView, doc, PHOTOGRAPHY_PATH).execute(photographyFiles, photographyFiles, photographyFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return photographyFiles;
    }

    public ArrayList<String> getUserFiles(View rootView, Activity activity) {
        Document doc;
        usersFiles.clear();
        try {
            doc = Jsoup.connect(MAIN_URL + USERS_PATH + INDEX_PHP).timeout(10 * 1000).get();
            new GetLinks(activity, rootView, doc, USERS_PATH).execute(usersFiles, usersFiles, usersFiles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersFiles;
    }

    public ArrayList<String> getAllFiles(Activity activity, boolean fromShakeListener) {
        allFiles.clear();
        new URLs.GetAllLinks(activity, fromShakeListener).execute(
                URLs.FLAT_PATH,
                URLs.GNOW_PATH,
                URLs.MATERIAL_PATH,
                URLs.MINIMAL_PATH,
                URLs.PHOTOGRAPHY_PATH,
                URLs.POLY_PATH,
                URLs.USERS_PATH
        );
        return allFiles;
    }

    public static class GetAllLinks extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        final Activity activity;
        final boolean fromShakeListener;

        public GetAllLinks(Activity activity, boolean fromShakeListener) {
            this.activity = activity;
            this.fromShakeListener = fromShakeListener;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(activity, "", activity.getString(R.string.selecting_wallpaper), true);
            super.onPreExecute();
        }

        /**
         * Sholud contain every path as args
         **/
        @Override
        protected String doInBackground(String... everyPath) {
            allFiles.clear();
            for (String aPath : everyPath) {
                Document doc = null;
                try {
                    doc = Jsoup.connect(MAIN_URL + aPath + INDEX_PHP).timeout(10*1000).get();
                } catch (IOException ignored) {
                }
                for (Element file : doc.select("li a")) {
                    if (!file.attr("href").contains("NOT_AUTHORIZED")) {
                        allFiles.add(MAIN_URL + aPath + file.attr("href").replace("./", "/"));
                        if (Utils.Debug.isDebug)
                            Log.d("URL", MAIN_URL + aPath + file.attr("href").replace("./", "/"));
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (fromShakeListener) {
                try {
                    String url = allFiles.get(new Random().nextInt(allFiles.size()));
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(activity.getApplicationContext());
                    InputStream inputStream = new URL(url).openStream();
                    wallpaperManager.setStream(inputStream);
                } catch (IOException ignored) {}
            }
            dialog.dismiss();

            if (fromShakeListener) {
                Toast.makeText(activity, R.string.done, Toast.LENGTH_SHORT).show();
                activity.startActivity(Utils.homeIntent());
            }

            super.onPostExecute(s);
        }
    }

    private class GetLinks extends AsyncTask<ArrayList<String>, ArrayList<String>, ArrayList<String>> {

        private final Document doc;
        private final String path;
        private final View rootView;
        private final Activity activity;
        private ArrayList<String> stringArrayList;

        GetLinks(Activity activity, View rootView, Document doc, String path) {
            this.activity = activity;
            this.doc = doc;
            this.rootView = rootView;
            this.path = path;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayList<String> doInBackground(ArrayList<String>... arrayLists) {
            arrayLists[0].clear();
            for (Element file : doc.select("li a")) {
                if (!file.attr("href").contains("NOT_AUTHORIZED"))
                    arrayLists[0].add(MAIN_URL + path + file.attr("href").replace("./", "/"));
            }
            stringArrayList = arrayLists[0];
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> arrayList) {
            TextView noWallpapersAvailable = (TextView)rootView.findViewById(R.id.no_wallpapers_available);
            GridView gridView = (GridView)rootView.findViewById(R.id.grid_view);
            if (stringArrayList.size() == 0) {
                gridView.setVisibility(View.GONE);
                noWallpapersAvailable.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(arrayList);
        }
    }
}