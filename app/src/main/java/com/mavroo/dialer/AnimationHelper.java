package com.mavroo.dialer;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

public class AnimationHelper {
    private static final AnimationHelper ourInstance = new AnimationHelper();

    public static AnimationHelper getInstance() {
        return ourInstance;
    }

    private AnimationHelper() {
    }

    public static void animScaleSquare(View view, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f);
        anim.setFillAfter(true);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    public static void animateJump(Context context, View view) {
        Animation animation;

        animation = AnimationUtils.loadAnimation(context, R.anim.anim_jump);
        view.startAnimation(animation);
    }

    public static void animateJumpLow(Context context, View view) {
        Animation animation;

        animation = AnimationUtils.loadAnimation(context, R.anim.anim_jump_low);
        view.startAnimation(animation);
    }
}
