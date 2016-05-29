package de.dukadinov.lizer;

import java.io.Serializable;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class LizerFragment extends Fragment implements OnTouchListener {

public interface Input extends Serializable {

    Drawable getNextImage(int width, int height);

}

    private final DecelerateInterpolator interpolator = new DecelerateInterpolator(
            0.5f);
    private Input input;

    private int animationDuration;
    private View imageView1;
    private View imageView2;

    private int touchX;
    private int touchY;
    private boolean dragging;
    private int clickToDragTolerance = 50;
    private int x2;

    public static LizerFragment newInstance(Input input) {
        LizerFragment lizerFragment = new LizerFragment();
        Bundle args = new Bundle();
        args.putSerializable("input", input);
        lizerFragment.setArguments(args);
        return lizerFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        input = (Input) getArguments().getSerializable("input");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        input = (Input) getArguments().getSerializable("input");
        View rootView = inflater.inflate(R.layout.lizer_layout, container,
                false);
        animationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);
        rootView.setOnTouchListener(this);

        imageView1 = (View) rootView.findViewById(R.id.imageView1);
        imageView2 = (View) rootView.findViewById(R.id.imageView2);
        imageView2.setAlpha(0);

        container.getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ((ImageView) imageView1).setImageDrawable(input.getNextImage(
                        imageView1.getWidth(), imageView1.getHeight()));
                ((ImageView) imageView2).setImageDrawable(input.getNextImage(
                        imageView1.getWidth(), imageView1.getHeight()));
            }
        });
        return rootView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = Math.round(event.getX());
        int y = Math.round(event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchX = x;
                touchY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!dragging) {
                    // distinguish 'click' from 'drag'
                    if (Math.abs(x - touchX) > clickToDragTolerance
                            || Math.abs(y - touchY) > clickToDragTolerance) {
                        dragging = true;
                        dragStart(touchX, touchY);
                    }
                }
                if (dragging) {
                    drag(x, y);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragging) {
                    dragging = false;
                    dragEnd();
                } else {
                    click(x, y);
                }
                break;
            default:
                return false;
        }

        return true;
    }

    private void dragStart(int x, int y) {
        // imageView1.setX(0);
        imageView2.setAlpha(0);
        imageView2.setPivotX(imageView2.getWidth() / 2);
        imageView2.setPivotY(imageView2.getHeight() / 2);
        imageView2.setVisibility(View.VISIBLE);
        // TODO Auto-generated method stub

    }

    private void drag(int x, int y) {
        x2 = x - touchX;
        imageView1.setTranslationX(x2);
        imageView1.setAlpha(imageView1.getWidth() / (float) Math.abs(x2) - 1);
        imageView2.setAlpha((float) Math.abs(x2) / imageView1.getWidth());
        float scaleFactor = getImageView2ScaleFactor();
        imageView2.setScaleX(scaleFactor);
        imageView2.setScaleY(scaleFactor);
    }

    private void dragEnd() {
        imageView2.setScaleX(1);
        imageView2.setScaleY(1);
        if (-x2 > LizerSettings.DRAG_FOR_NEXT * imageView1.getWidth()) {
            animateNext(true);
        } else if (x2 > LizerSettings.DRAG_FOR_NEXT * imageView1.getWidth()) {
            animateNext(false);
        } else {
            animateBack();
        }
    }

    private void animateNext(boolean left) {
        AnimationSet set1 = new AnimationSet(true);
        set1.addAnimation(new AlphaAnimation(1, 0));
        set1.addAnimation(new TranslateAnimation(x2, (left ? -1 : 1) * imageView1.getWidth(), 0,
                0));
        set1.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imageView1.setTranslationX(0);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((ImageView) imageView1)
                        .setImageDrawable(((ImageView) imageView2)
                                .getDrawable());
                imageView1.setX(0);
                imageView1.setTranslationX(0);
                imageView1.setAlpha(1);

                // post following code to avoid flicker
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView2.setVisibility(View.GONE);
                        imageView2.setAlpha(0);
                        ((ImageView) imageView2).setImageDrawable(input
                                .getNextImage(imageView1.getWidth(),
                                        imageView1.getHeight()));
                    }
                });
            }
        });
        set1.setDuration(animationDuration);
        set1.setInterpolator(interpolator);

        float scaleFactor = getImageView2ScaleFactor();
        AnimationSet set2 = new AnimationSet(true);
        set2.addAnimation(new ScaleAnimation(scaleFactor, 1, scaleFactor, 1,
                imageView2.getWidth() / 2, imageView2.getHeight() / 2));
        AlphaAnimation aa2 = new AlphaAnimation((float) Math.abs(x2)
                / imageView1.getWidth(), 1);
        set2.addAnimation(aa2);
        set2.setDuration(animationDuration);
        set2.setInterpolator(interpolator);
        set2.setFillAfter(true);

        imageView2.setAlpha(1);
        imageView2.startAnimation(set2);
        imageView1.startAnimation(set1);
    }

    private float getImageView2ScaleFactor() {
        float scaleFactor = LizerSettings.SCALE_IN_START_FROM + (1 - LizerSettings.SCALE_IN_START_FROM)
                * Math.abs(x2) / imageView1.getWidth();
        return scaleFactor;
    }

    private void animateBack() {
        AnimationSet set1 = new AnimationSet(true);
        set1.addAnimation(new AlphaAnimation(imageView1.getWidth()
                / (float) Math.abs(x2) - 1, 1));
        set1.addAnimation(new TranslateAnimation(x2, 0, 0, 0));
        set1.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                imageView1.setTranslationX(0);
                imageView1.setAlpha(1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView2.setVisibility(View.GONE);
            }
        });

        set1.setInterpolator(interpolator);
        set1.setDuration(animationDuration);

        float scaleFactor = getImageView2ScaleFactor();
        AnimationSet set2 = new AnimationSet(true);
        set2.addAnimation(new ScaleAnimation(scaleFactor, LizerSettings.SCALE_IN_START_FROM,
                scaleFactor, LizerSettings.SCALE_IN_START_FROM, imageView2.getWidth() / 2,
                imageView2.getHeight() / 2));
        set2.addAnimation(new AlphaAnimation(1, 0));
        set2.setDuration(animationDuration);
        set2.setInterpolator(interpolator);
        // set2.setFillAfter(true);

        imageView1.startAnimation(set1);
        imageView2.startAnimation(set2);
    }

    private void click(int x, int y) {
        x2 = 0;
        imageView2.setVisibility(View.VISIBLE);
        animateNext(true);
        // TODO Auto-generated method stub

    }
}
