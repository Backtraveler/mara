package com.dmonk.mara.Utils.layoutTransition

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs

enum class GestureType{
    SCROLL,FLING, INTERRUPTED_SCROLL
}
enum class GestureDirection{
    UP,DOWN,RIGHT,LEFT
}
data class GestureEvent(val type: GestureType,val direction: GestureDirection, val distance : Float = 0f)

interface GestureCallback {
    fun onGestureEvent(gestureEvent: GestureEvent)
}
interface BufferCallback {
    fun onNextGestureEvent(gestureEvent: GestureEvent)
    fun onIncompleteGestureEvent()
}

class GestureTimerThread(private val callback: BufferCallback, private val context: Context) : Thread() {
    private var cancelTimer = false
    private var counter = 0


    override fun run() {
        while (!cancelTimer){
            Log.d("GestureTimerThread: ","COUNTER : $counter.")

            sleep(10)
            if (++counter == 5 && !cancelTimer){
                (context as Activity).runOnUiThread {
                    cancelTimer = true
                    callback.onIncompleteGestureEvent()
                }
            }

        }

    }
    fun cancel(){
      cancelTimer = true
        Log.d("GestureTimerThread: ","Timer is canceled")
    }

}

class GestureBuffer( private val callback: BufferCallback, private val context: Context): GestureCallback {
    enum class modType{
        ADD_GESTURE,GET_GESTURE,GESTURE_INTERRUPT
    }


    private val gestureStack = Stack<GestureEvent>()
    private var isGestureHandled = false
    private var timerTriggered = false
    private lateinit var timerThread: GestureTimerThread


    @Synchronized
    private fun modifyStack(type: modType, gestureEvent: GestureEvent?) {

        // Check for up gesture timer
        if(timerTriggered){
            // Cancel trigger as fling gesture has been broadcast
            timerThread.cancel()

            // Reset flag
            timerTriggered = false
        }



        // Verify type/call origin
        if (type == modType.ADD_GESTURE){

            Log.d("GestureBuffer:","Add gesture triggered.")
            // Check if prior gesture is still being handled.
            if(isGestureHandled) {
                // Push to stack if yes.
                gestureStack.push(gestureEvent)
                Log.d("GestureBuffer:","Gesture stored Stack Size $gestureStack")

            }else{
                Log.d("GestureBuffer:","Gesture passed to SceneManager.")

                // Set flag as gesture is still being handled.
                isGestureHandled = true
                // Pass to SceneManager.
                callback.onNextGestureEvent(gestureEvent!!)

            }

        }else if (type == modType.GET_GESTURE){

            // Check gesture stack.
            if(gestureStack.isNotEmpty()) {
                // if not empty handle next.
                Log.d("GestureBuffer:","Gesture Stack ${gestureStack.size}")
                callback.onNextGestureEvent(gestureStack.pop())
            }else{
                // if empty set to false for trigger.
                isGestureHandled = false
            }
        }
    }
    override fun onGestureEvent(gestureEvent: GestureEvent) {
        Log.d("GestureBuffer:","gesture triggered.")

        modifyStack(modType.ADD_GESTURE,gestureEvent)
    }

    fun getNextGestureEvent(){
        Log.d("GestureBuffer: ","Get Next Gesture Event called by Scene Manager.")
        Log.d("GestureBuffer:","Gesture Stack ${gestureStack.size}")

        modifyStack(modType.GET_GESTURE,null)
    }
    fun onGestureInterrupted() {
        Log.d("GestureBuffer: ","Motion Up registered.")
        // Set potential fling direction from gesture count.
        timerThread = GestureTimerThread(callback,context)
        timerThread.start()

        // Set timer triggered flag
        timerTriggered = true
    }

}

class GestureListener(val callback: GestureCallback): GestureDetector.SimpleOnGestureListener(), GestureDetector.OnGestureListener{

    private var yScrollAverage = 0f
    private var averageCounter = 0
    private var gestureCount = 0

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        Log.d(TAG, "On single tap.")

        return true
    }

    /**
     * Handles scrolling gestures. Paused.
     */
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {

        val diffY = e2!!.y - e1!!.y
        val diffX = e2.x - e1.x
        val df = DecimalFormat("#.###")
        df.roundingMode = RoundingMode.CEILING
        var distance = abs(diffY/10000)



        Log.d(TAG, "E1 equals : ${e1.y}")
        Log.d(TAG, "E2 equals : ${e2.y}")


        // Get scroll direction
        try {

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SCROLL_THRESHOLD) {
                    if (diffX > 0) {
                        Log.d(TAG, "Right")
                    } else {
                        Log.d(TAG, "Left")
                    }
                }
            } else if (Math.abs(diffY) > SCROLL_THRESHOLD) {

                // Dampening gesture.
                 // increment avg
                yScrollAverage += distance
                // increment counter
                // check average to trigger gesture callback
                if(++averageCounter == DAMPENING_VALUE){
                    // get average
                    distance = (yScrollAverage/ DAMPENING_VALUE)

                    // reset
                    yScrollAverage = 0f
                    averageCounter = 0

                    // Increment gesture count
                    ++gestureCount


                if (e1.y < e2.y) {
                    // Get average of 10 gestures
                    // Trigger callback passing average.
                    //callback.onGestureEvent(GestureEvent(GestureType.SCROLL,GestureDirection.DOWN,distance))
                    Log.d(TAG, "Scrolling Down")
                } else {
                    //callback.onGestureEvent(GestureEvent(GestureType.SCROLL,GestureDirection.UP,distance))
                    Log.d(TAG, "Scrolling UP")
                    }
                }
            }

        } catch (exception: Exception) {
            exception.printStackTrace()
        }


        return super.onScroll(e1, e2, distanceX, distanceY)
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
                    callback.onGestureEvent(GestureEvent(GestureType.FLING,GestureDirection.DOWN,0f))
                    Log.d(TAG, "Bottom")
                } else {
                    callback.onGestureEvent(GestureEvent(GestureType.FLING,GestureDirection.UP,0f))
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
        private const val SWIPE_THRESHOLD = 200
        private const val SWIPE_VELOCITY_THRESHOLD = 200
        private const val SCROLL_THRESHOLD = 5
        private const val DAMPENING_VALUE = 5
        private const val INITIAL_DISTANCE = 100000000000f
        const val TAG = "GestureListener"
    }
}

