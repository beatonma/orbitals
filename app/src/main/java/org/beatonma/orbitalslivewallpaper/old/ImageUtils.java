package org.beatonma.orbitalslivewallpaper.old;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Michael on 10/02/2015.
 */
public class ImageUtils {
    private final static String TAG = "ImageUtils";
    Context context;
    String cachedImage = "";
    int maxSize = 0;
    String mode = "";

    public ImageUtils(Context context, String type) {
        this.context = context;
        this.mode = type;

        if (type.equals("lwp")) {
            cachedImage = getSharedPreference("background_file");
            if (cachedImage.equals("")) {
                cachedImage = context.getCacheDir() + File.separator + "lwp_bg_image";
            }
        }
        else {
            cachedImage = context.getCacheDir() + File.separator + "dream_bg_image";
        }
    }

    public void copyFileFromUri(Uri fileUri) {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            ContentResolver content = context.getContentResolver();
            inputStream = content.openInputStream(fileUri);
            String type = content.getType(fileUri);

            // create a directory
            File saveDirectory = new File(context.getCacheDir() + File.separator);
            // create direcotory if it doesn't exists
            saveDirectory.mkdirs();

            //String outFile = saveDirectory + File.separator + "background_image";
            String outFile = "";
            Log.d(TAG, "ImageUtils mode=" + mode);
            if (mode.equals("lwp")) {
                outFile = context.getCacheDir() + File.separator + "lwp_bg_image";
            }
            else {
                outFile = cachedImage;
            }

            outputStream = new FileOutputStream(outFile);
            if (outputStream != null) {
                Log.d(TAG, "Output Stream Opened successfully");
            }

            byte[] buffer = new byte[1000];
            int bytesRead = 0;
            while ((bytesRead = inputStream.read(buffer, 0, buffer.length)) >= 0) {
                outputStream.write(buffer, 0, buffer.length);
            }
            Log.d(TAG, "File copied to " + outFile);
        }
        catch (Exception e){
            Log.e(TAG, "Error caching file: " + e.toString());
        }
        finally {

        }
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public void processBackgroundImage(Uri uri) {
        Log.d(TAG, "Processing image: " + uri);
        new ProcessBackgroundImage().execute(uri);
    }

    public class ProcessBackgroundImage extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... uri) {
            Uri fileUri = uri[0];
            Log.d(TAG, "Processing file: " + fileUri);
            File file = new File(cachedImage);
            Uri cachedFile = Uri.fromFile(file);

            ContentResolver cR = context.getContentResolver();
            String mimeType = cR.getType(fileUri);
            if (mimeType == null) {
                mimeType = cR.getType(cachedFile);
            }
            if (mimeType == null) {
                mimeType = getMimeType(cachedFile.toString());
            }
            Log.d(TAG, "mimeType=" + mimeType);

            if (isImageTooBig()) {
                Log.d(TAG, "Image is too big. Resizing to fit maxSize=" + maxSize);
                resizeImage();
            }

            writeUriToSharedPreference(cachedFile.toString());
            writeMimeTypeToSharedPreference(mimeType);

            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String file) {

        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public boolean isImageTooBig() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(cachedImage, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        Log.d(TAG, "image dimensions:" + imageWidth + "x" + imageHeight);

        if (imageHeight > getMaxSize() || imageWidth > getMaxSize()) {
            Log.d(TAG, "Image is too big and needs resizing.");
            return true;
        }
        else {
            Log.d(TAG, "Image size is okay - no resizing necessary.");
            return false;
        }
    }

    public int getMaxSize() {
        if (maxSize == 0) {
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Point p = new Point();
            wm.getDefaultDisplay().getSize(p);
            maxSize = Math.max(p.x, p.y);
            Log.d(TAG, "Max image size =" + maxSize + "*" + maxSize);
            return maxSize;
        }
        else {
            return maxSize;
        }
    }

    public void resizeImage() {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(cachedImage, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, getMaxSize(), getMaxSize());

        // Decode bitmap with inSampleSize set and write back to file
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(cachedImage, options);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);

        File outputFile = new File(cachedImage);
        byte[] bitmapData = bos.toByteArray();

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(bitmapData);
        }
        catch (Exception e) {
            Log.e(TAG, "Error writing resized image to file: " + e.toString());
        }
    }

    private void writeUriToSharedPreference(String uri) {
        setSharedPreference("image_uri", uri);
    }

    private void writeMimeTypeToSharedPreference(String mimeType) {
        setSharedPreference("mime", mimeType);
    }

    private void setSharedPreference(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences("preferences", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String getSharedPreference(String key) {
        return context.getSharedPreferences("orbitalsLWPsettings", Activity.MODE_PRIVATE).getString(key, "");
    }
}
