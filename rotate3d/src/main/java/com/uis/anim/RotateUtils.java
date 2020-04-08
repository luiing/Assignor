package com.uis.anim;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PointF;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

public class RotateUtils {

    public static long duration = 500;

    public static boolean isRotate(Intent intent){
        return intent != null && intent.getBooleanExtra(RotateCall.ROTATE,false);
    }

    // 逆时针旋转90
    public static void applyFirstRotation(RotateCall call,ViewGroup layout,float start, float end) {
        // Find the center of the container
        PointF point = getCenter(layout);
        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
                point.x, point.y, 310.0f, true);
        rotation.setDuration(duration);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(call));
        layout.startAnimation(rotation);
    }

    public static void applyLastRotation(ViewGroup layout,float start, float end) {
        // Find the center of the container
        PointF point = getCenter(layout);
        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end,
                point.x, point.y, 310.0f, false);
        rotation.setDuration(duration);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateDecelerateInterpolator());
        layout.startAnimation(rotation);
    }

    public static PointF getCenter(ViewGroup layout){
        float centerX = layout.getWidth() / 2.0f;
        float centerY = layout.getHeight() / 2.0f;
        if(centerX <= 0) {
            centerX = Resources.getSystem().getDisplayMetrics().widthPixels / 2.0f;
            centerY = Resources.getSystem().getDisplayMetrics().heightPixels / 2.0f;
        }
        return new PointF(centerX,centerY);
    }
}
