package com.github.yeriomin.yalpstore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class BitmapManager {

    private static final long VALID_MILLIS = 1000*60*60*24*7;

    private Context context;

    public BitmapManager(Context context) {
        this.context = context;
    }

    public Bitmap getBitmap(String url) {
        return getBitmap(url, false);
    }

    public Bitmap getBitmap(String url, boolean fullSize) {
        File cached = getFileName(url);
        Bitmap bitmap;
        if (!cached.exists() || !isValid(cached)) {
            bitmap = downloadBitmap(url, fullSize);
            if (null != bitmap) {
                cacheBitmap(bitmap, cached);
            }
        } else {
            bitmap = getCachedBitmap(cached);
        }
        return bitmap;
    }

    private File getFileName(String urlString) {
        return new File(context.getCacheDir(), String.valueOf(urlString.hashCode()) + ".png");
    }

    static private boolean isValid(File cached) {
        return cached.lastModified() + VALID_MILLIS > System.currentTimeMillis();
    }

    static private Bitmap getCachedBitmap(File cached) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inDither = false;
            return BitmapFactory.decodeStream(new FileInputStream(cached), null, options);
        } catch (FileNotFoundException e) {
            // We just checked for that
            return null;
        }
    }

    static private void cacheBitmap(Bitmap bitmap, File cached) {
        try {
            FileOutputStream out = new FileOutputStream(cached);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static private Bitmap downloadBitmap(String url, boolean fullSize) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            connection.setConnectTimeout(3000);
            InputStream input = connection.getInputStream();

            BitmapFactory.Options options = new BitmapFactory.Options();
            if (!fullSize) {
                options.inSampleSize = 4;
            }
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeStream(input, null, options);
        } catch (IOException e) {
            Log.e(BitmapManager.class.getName(), "Could not get icon from " + url);
        }
        return null;
    }
}
