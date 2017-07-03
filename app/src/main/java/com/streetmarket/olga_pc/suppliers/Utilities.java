package com.streetmarket.olga_pc.suppliers;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.streetmarket.olga_pc.suppliers.beans.ItemImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

/**
 * Created by Olga-PC on 5/12/2016.
 */
public class Utilities {
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void saveImage(Context mContext, ItemImage image) {
        File photo = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), image.getFileName());

        if (!photo.exists()) {
            try (FileOutputStream fos = new FileOutputStream(photo.getPath());) {
                fos.write(image.getImage());
                fos.close();
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }
        }

    }

    public static boolean isImageExists(Context mContext, String code) {
        boolean fl = false;
        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && dir.exists() && dir.isDirectory()) {
            String[] arr = dir.list(new ImageFilenameFilter(code));
            fl = arr != null && arr.length > 0;
        }
        return fl;
    }

    public static File getImageFile(Context mContext, String code) {
        File f = null;
        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && dir.exists() && dir.isDirectory()) {
            File[] arr = dir.listFiles(new ImageFilenameFilter(code));
            f = arr != null && arr.length > 0 ? arr[0] : null;
        }
        return f;
    }

    private static class ImageFilenameFilter implements FilenameFilter {
        private String code;

        public ImageFilenameFilter(String code) {
            this.code = code;
        }

        @Override
        public boolean accept(File arg0, String fileName) {
            return fileName.contains(code);
        }
    }

    public static final int getColor(Context context, @ColorRes int id) {
        // final int version = Build.VERSION.SDK_INT;
        return ContextCompat.getColor(context, id);
    }
}
