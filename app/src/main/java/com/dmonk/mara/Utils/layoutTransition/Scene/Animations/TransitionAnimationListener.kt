package com.dmonk.mara.Utils.layoutTransition.Scene.Animations

import android.animation.Animator

abstract class TransitionAnimationListener : Animator.AnimatorListener {
    /**
     * Keeping track of layout changes during scrolling gesture.
     */
    abstract fun onContentViewResized(newHeight: Float, newWidth: Float)

    override fun onAnimationStart(p0: Animator?) {
    }

    override fun onAnimationEnd(p0: Animator?) {
    }

    override fun onAnimationCancel(p0: Animator?) {
    }

    override fun onAnimationRepeat(p0: Animator?) {
    }
}