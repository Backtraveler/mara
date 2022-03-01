package com.dmonk.mara.Utils.layoutTransition.Scene.Animations

import android.animation.ArgbEvaluator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.dmonk.mara.Utils.layoutTransition.GestureDirection
import com.dmonk.mara.Utils.layoutTransition.Scene.SceneState
import com.dmonk.mara.Utils.layoutTransition.Scene.StateSpecs

class SceneAnimator {
    private val FLING_DURATION = 750L
    fun animateFling(view : View, header : TextView, specs: StateSpecs, listener: TransitionAnimationListener) {
        Log.d("MiddleToTop:","Full Transition Sequence Called.")

        val width = PropertyValuesHolder.ofInt("width",specs.startingX,specs.endingX)
        val height = PropertyValuesHolder.ofInt("height",specs.startingY,specs.endingY)
        val textSize = PropertyValuesHolder.ofFloat("text size",specs.startingHeaderTextSize,specs.endingHeaderTextSize)
        val textAlpha = PropertyValuesHolder.ofFloat("text alpha",specs.startingHeaderTextAlpha,specs.endingHeaderTextAlpha)
        val textBackgroundColor = PropertyValuesHolder.ofInt("color",specs.startingBackgroundColor,specs.endingBackgroundColor)
        textBackgroundColor.setEvaluator(ArgbEvaluator())

        val animator = ValueAnimator.ofPropertyValuesHolder(width,height,textSize,textAlpha,textBackgroundColor)
        animator.duration = FLING_DURATION
        animator.addListener(listener)
        animator.interpolator = FastOutSlowInInterpolator()
        animator.addUpdateListener { valueAnimator ->

            val newWidth = valueAnimator.getAnimatedValue("width") as Int
            val newHeight = valueAnimator.getAnimatedValue("height") as Int
            val newTextSize = valueAnimator.getAnimatedValue("text size") as Float
            val newAlpha = valueAnimator.getAnimatedValue("text alpha") as Float
            val newColor = valueAnimator.getAnimatedValue("color") as Int
            Log.d("SceneAnimator:","New Height : $newHeight and New Width $newWidth.")

            resizeLayout(view, newHeight , newWidth, specs.state, specs.direction)

            header.textSize = newTextSize
            header.alpha = newAlpha
            header.setBackgroundColor(newColor)

        }
        animator.start()

    }
    fun animateFling(view : View, start : Float, end : Float, listener: TransitionAnimationListener) {
        Log.d("MiddleToTop:","Full Transition Sequence Called.")

        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = 400
        animator.addListener(listener)
        animator.interpolator = FastOutSlowInInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            resizeLayout(view,animatedValue.toInt())

        }

        animator.start()
    }
    fun animateFling(view : View, start : Float, end : Float) {
        Log.d("MiddleToTop:","Full Transition Sequence Called.")

        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = 400
        animator.interpolator = FastOutSlowInInterpolator()
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            resizeLayout(view,animatedValue.toInt())

        }

        animator.start()
    }
    fun animateScroll(view : View, end : Float, limit : Float, listener: TransitionAnimationListener) {
        //Animate
        resizeLayout(view,end.toInt())
        listener.onContentViewResized(end,0f)


    }

    private fun transitionIsCompleted(end: Float, limit: Float): Boolean {
        return  end >= limit
    }

    private fun resizeLayout(layout: View, newHeight: Int = layout.height, newWidth : Int = layout.width){
        val p = LinearLayout.LayoutParams(newWidth,newHeight)
        layout.layoutParams = p
        layout.requestLayout()
    }
    private fun resizeLayout(layout: View, newHeight: Int = layout.height, newWidth : Int = layout.width, state: SceneState,direction: GestureDirection){
        val p = LinearLayout.LayoutParams(newWidth,newHeight)
        if (state == SceneState.INDEXED || ((state == SceneState.FOCUSED || state == SceneState.STAGED) && direction == GestureDirection.UP))
            p.topMargin = 25
        else
            p.topMargin = 0

        layout.layoutParams = p
        layout.requestLayout()
    }

}