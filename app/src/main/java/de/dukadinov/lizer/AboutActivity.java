package de.dukadinov.lizer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class AboutActivity extends Activity {

    private View toAnimate;
    private ObjectAnimator waiting;
    private int activeFallingAnims;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        toAnimate = findViewById(R.id.about_j);

        scheduleAnimation();
    }

    private void scheduleAnimation() {
        waiting = ObjectAnimator.ofFloat(toAnimate, "rotation", 0, 3 * 360);
        waiting.setRepeatCount(1);
        waiting.setInterpolator(new AccelerateDecelerateInterpolator());
        waiting.setDuration(1000);
        waiting.setStartDelay(2500);

        waiting.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (waiting != null) {
                    scheduleAnimation();
                }
            }
        });

        waiting.start();
    }

    public void jump(View view) {
        if (waiting != null) {
            ObjectAnimator toEnd = waiting;
            waiting = null;
            toEnd.end();
        }

        float newY = view.getTranslationY() - 50;
        ObjectAnimator flying = ObjectAnimator.ofFloat(toAnimate, "translationY", newY + 50, newY);
        flying.setDuration(150);
        flying.setInterpolator(new DecelerateInterpolator());

        view.setTranslationY(newY);
        ObjectAnimator falling = ObjectAnimator.ofFloat(toAnimate, "translationY", newY, 0);
        falling.setDuration(1500 + -5 * (int) newY);
        falling.setInterpolator(new AccelerateInterpolator());
        falling.setStartDelay(flying.getDuration());
        falling.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                activeFallingAnims++;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (--activeFallingAnims == 0) {
                    scheduleAnimation();
                }
            }
        });

        flying.start();
        falling.start();
    }
}