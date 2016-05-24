package de.dukadinov.lizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;

public class ImageLoader {

    public static Bitmap getImageFromFile(String filename) {
        return BitmapFactory.decodeFile(filename);
    }

    public static Bitmap scaleImage(Object image, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap((Bitmap) image, newWidth, newHeight, false);
    }

    public static Bitmap getImageFromFile(String fileName, int reqWidth, int reqHeight) {
        if (fileName == null) {
            return null;
        }

        FileInputStream is = null;
        try {
            is = new FileInputStream(fileName);
            return streamToImage(is, reqWidth, reqHeight);
        } catch (IOException e) {
            return null;
        } finally {
            Util.close(is);
        }
    }

    public static Bitmap getImageFromApp(int drawableId, int reqWidth, int reqHeight, Resources resources) {
        InputStream is = null;
        try {
            is = resources.openRawResource(drawableId);
            return streamToImage(is, reqWidth, reqHeight);
        } catch (IOException e) {
            return null;
        } finally {
            Util.close(is);
        }
    }

    private static Bitmap streamToImage(InputStream is, int reqWidth, int reqHeight) throws IOException {
        BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, true);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inPurgeable = true;
        o.inInputShareable = true;
        o.inSampleSize = calculateInSampleSize(decoder, reqWidth, reqHeight);

        float q = Math.min(decoder.getWidth() / o.inSampleSize / (float) reqWidth, decoder.getHeight() / o.inSampleSize
                / (float) reqHeight);

        int qWidth = (int) (reqWidth * q);
        int qHeight = (int) (reqHeight * q);

        int left = (decoder.getWidth() / o.inSampleSize - qWidth) / 2;
        int top = (decoder.getHeight() / o.inSampleSize - qHeight) / 2;
        Rect rect = new Rect(left, top, left + o.inSampleSize * qWidth, top + o.inSampleSize * qHeight);
        Bitmap decodedRegion = decoder.decodeRegion(rect, o);
        return Bitmap.createScaledBitmap(decodedRegion, reqWidth, reqHeight, false);
    }

    public static Bitmap getRoundImageFromFile(String filename, int rx, int ry) {
        return getRoundedCornerBitmap(getImageFromFile(filename), rx, ry);
    }

    public static Bitmap getRoundImageFromFile(String filename, int reqWidth, int reqHeight, int rx, int ry) {
        return getRoundedCornerBitmap(getImageFromFile(filename, reqWidth, reqHeight), rx, ry);
    }

    public static Bitmap getRoundImageFromApp(int drawableId, int reqWidth, int reqHeight, int rx, int ry,
            Resources resources) {
        return getRoundedCornerBitmap(getImageFromApp(drawableId, reqWidth, reqHeight, resources), rx, ry);
    }

    private static int calculateInSampleSize(BitmapRegionDecoder decoder, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = decoder.getHeight();
        final int width = decoder.getWidth();
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float rx, float ry) {
        if (rx <= 0 || ry <= 0 || bitmap == null) {
            return bitmap;
        }

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, rx, ry, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
