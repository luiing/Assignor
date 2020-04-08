package com.uis.anim;

import android.view.animation.Animation;

import java.lang.ref.WeakReference;

public class DisplayNextView implements Animation.AnimationListener {

    private WeakReference<RotateCall> call;

    public DisplayNextView(RotateCall call) {
        if(call != null) {
            this.call = new WeakReference<>(call);
        }
    }

    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void onAnimationEnd(Animation animation) {
        if(call != null) {
            RotateCall rotate = call.get();
            if(rotate != null) {
                rotate.rotateNextPage();
            }
            call = null;
        }
    }
}
