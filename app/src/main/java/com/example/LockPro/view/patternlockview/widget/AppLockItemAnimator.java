package com.example.LockPro.view.patternlockview.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;

import androidx.recyclerview.widget.RecyclerView;

//Locked to unlocked: RTL
public class AppLockItemAnimator extends SimpleDefaultItemAnimator {

    private boolean isLocked;

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    @Override
    void animateRemoveImpl(RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;
        final ViewPropertyAnimator animation = view.animate();
        mRemoveAnimations.add(holder);
        //
        AnimatorListenerAdapter animatorListenerAdapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animator) {
                dispatchRemoveStarting(holder);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animation.setListener(null);
                view.setAlpha(1);
                view.setTranslationX(0);
                dispatchRemoveFinished(holder);
                mRemoveAnimations.remove(holder);
                dispatchFinishedWhenDone();
            }
        };

        int measureWidth = view.getMeasuredWidth();
        int width = (measureWidth <= 0) ? view.getMinimumWidth() : measureWidth;
        if (width > 0) {
            animation.setDuration(getRemoveDuration())
                    .translationX(isLocked ? -width : width)
                    .alpha(0)
                    .setListener(animatorListenerAdapter)
                    .start();
        } else {
            animation.setDuration(getRemoveDuration())
                    .alpha(0)
                    .setListener(animatorListenerAdapter)
                    .start();
        }
    }
}
