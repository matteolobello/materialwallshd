package com.lob.mwhd.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.lob.mwhd.R;
import com.lob.mwhd.URLs;
import com.lob.mwhd.helpers.GetWhichFragment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadWallpaperFragment extends Fragment {

    private final int SELECT_PHOTO = 100;

    private FloatingActionButton uploadButton;
    private Button imageSelector;
    private ProgressDialog dialog;
    private View rootView;
    private EditText title, credits;

    private int serverResponseCode = 0;
    private String upLoadServerUri = URLs.USER_UPLOAD_PHP_SCRIPT;
    private String uploadFilePath;

    private HttpURLConnection httpUrlConnection;
    private DataOutputStream dataOutputStream;
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";
    private int bytesRead, bytesAvailable, bufferSize;
    private byte[] buffer;
    private int maxBufferSize = 1024 * 1024;

    private int uploadFile(final String sourceFileUri) {
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            dialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(uploadButton, getActivity().getString(R.string.file_not_exists), Snackbar.LENGTH_LONG).show();
                }
            });
            return 0;
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setDoInput(true);
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setUseCaches(false);
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
                httpUrlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                httpUrlConnection.setRequestProperty("uploaded_file", sourceFileUri);

                dataOutputStream = new DataOutputStream(httpUrlConnection.getOutputStream());

                String nameString = title.getText().toString().replace(" ", "_");
                String creditsString = credits.getText().toString().replace(" ", "_");
                String extensionString = sourceFileUri.substring(sourceFileUri.lastIndexOf("."));

                String finalName = ("NOT_AUTHORIZED_" + nameString + "-" + creditsString).replace("-", "_") + extensionString;

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                        + finalName + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = httpUrlConnection.getResponseCode();

                if (serverResponseCode == 200) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Snackbar.make(uploadButton, getString(R.string.done_will_verify), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Snackbar.make(uploadButton, getString(R.string.error), Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            dialog.dismiss();
            return serverResponseCode;
        }
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() <= 0;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    uploadFilePath = getRealPathFromURI(getActivity(), selectedImage);
                    imageSelector.setText(R.string.image_selected);
                    imageSelector.setTextColor(Color.parseColor("#4CAF50"));
                }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.upload_wallpaper_fragment, container, false);

        title = (EditText) rootView.findViewById(R.id.wallpaper_name);
        credits = (EditText) rootView.findViewById(R.id.wallpaper_credits);

        imageSelector = (Button) rootView.findViewById(R.id.wallpaper_select);
        uploadButton = (FloatingActionButton) rootView.findViewById(R.id.uploadButton);

        imageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
            }
        });

        GetWhichFragment.fragment = null;
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadFilePath != null) {
                    if (isEmpty(title)) {
                        title.setError(getActivity().getString(R.string.insert_name));
                    } else {
                        if (isEmpty(credits)) {
                            credits.setError(getActivity().getString(R.string.insert_credits));
                        } else {
                            dialog = ProgressDialog.show(getActivity(), "", getActivity().getString(R.string.uploading_wallpaper), true);
                            new Thread(new Runnable() {
                                public void run() {
                                    uploadFile(uploadFilePath);
                                }
                            }).start();
                        }
                    }
                } else {
                    Snackbar.make(uploadButton, getString(R.string.select_wallpaper), Snackbar.LENGTH_LONG).show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
