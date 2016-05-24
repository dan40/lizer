package de.dukadinov.lizer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DemoContentProvider implements LizerFragment.Input {
    private static final long serialVersionUID = 1L;
    private final int[] imageIds;
    private transient final Resources resources;
    private int index;

    public DemoContentProvider(Resources resources) {
        this.resources = resources;
        imageIds = new int[]{R.drawable.flag1, R.drawable.flag2, R.drawable.flag3};
    }

    public Drawable getNextImage(int width, int height) {
        if (width == 0) {
            width = 500;
        }
        if (height == 0) {
            height = 500;
        }

        if (++index == imageIds.length) {
            index = 0;
        }
        Bitmap bitmap = ImageLoader.getImageFromApp(imageIds[index], width, height, resources);
        return new BitmapDrawable(bitmap);
    }
}
