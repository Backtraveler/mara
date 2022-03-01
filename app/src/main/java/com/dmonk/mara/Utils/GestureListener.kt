package com.dmonk.mara.Utils

import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import com.dmonk.mara.ViewModels.TouchEventModel

class GestureListener() :
    GestureDetector.SimpleOnGestureListener(), GestureDetector.OnGestureListener{


    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {

        Log.d(TAG, "DistanceX...$distanceX....DistanceY...$distanceY")

        return super.onScroll(e1, e2, distanceX, distanceY)
    }

    override fun onLongPress(e: MotionEvent) {
        super.onLongPress(e)
        Log.d(TAG, "Long press triggered at $e")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        var result = false
        Log.d(TAG, "fling action received...")
        try {
            val diffY = e2.y - e1.y
            val diffX = e2.x - e1.x
            Log.d(TAG, "X $diffX Y$diffY")
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        Log.d(TAG, "Right")
                    } else {
                        Log.d(TAG, "Left")
                    }
                    result = true
                }
            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    Log.d(TAG, "Bottom")
                } else {
                    Log.d(TAG, "Top")
                }
                result = true
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        return result
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
        const val TAG = "GestureListener"
    }
}