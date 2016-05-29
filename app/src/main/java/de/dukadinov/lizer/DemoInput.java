package de.dukadinov.lizer;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class DemoInput implements LizerFragment.Input {
    private static final long serialVersionUID = 1L;
    private final int[] imageIds;
    private transient final Resources resources;
    private int index;

    public DemoInput(Resources resources) {
        this.resources = resources;
        imageIds = new int[]{R.drawable.flag1, R.drawable.flag2, R.drawable.flag3};
    }

    public Drawable getNextImage(int width, int height) {
        Bitmap bitmap = ImageLoader.getImageFromApp(imageIds[index++ % imageIds.length], width, height, resources);
        return new BitmapDrawable(bitmap);
    }
}
