package com.joey.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;

public class ExportHelper {
    private final Activity activity;

    public ExportHelper(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public String shareFile(String base64Data, String fileName, String mimeType) {
        try {
            String safeName = fileName.replaceAll("[^\\w\\-.]", "_");

            byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
            File file = new File(activity.getCacheDir(), safeName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();

            Uri uri = FileProvider.getUriForFile(
                activity,
                activity.getPackageName() + ".fileprovider",
                file
            );

            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType(mimeType);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            final Intent chooser = Intent.createChooser(shareIntent, "Share");
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            activity.runOnUiThread(() -> activity.startActivity(chooser));

            return "ok";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
}
