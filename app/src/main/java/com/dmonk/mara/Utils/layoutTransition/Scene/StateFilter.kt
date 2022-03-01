package com.dmonk.mara.Utils.layoutTransition.Scene

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Point
import android.util.Log
import android.widget.LinearLayout
import com.dmonk.mara.R
import com.dmonk.mara.Utils.layoutTransition.*

/**
 * Re-defines gesture distance based in scene state.
 */
class StateFilter {
    companion object{
        private const val SCREEN_QUARTER_HEIGHT = 200f
        private  val SCREEN_MAIN_HEIGHT = (screenRectPx.height() * 0.75f).toInt()
        private const val SCREEN_ZERO_HEIGHT = 0f
        private  val MATCH_PARENT = screenRectPx.width()

    }
    private var height = 0

    fun reset(){
        // Reset filter vars.
        height = 0
    }

    fun getFilteredRange(direction: GestureDirection, rawDistance : Float, state: SceneState):Point{
        val dimens = Point()
        if (state == SceneState.HIDDEN) {
            if (direction == GestureDirection.UP) {
                height = (rawDistance * 1000f).toInt()
                dimens.x = height
                dimens.y = 200
            } else if (direction == GestureDirection.DOWN) {
                height = 0
                dimens.x = height
                dimens.y = 0
            }
        }else if (state == SceneState.STAGED) {
            if (direction == GestureDirection.UP) {
                height = ((rawDistance * 5000) + 400).toInt()
                dimens.x = height
                dimens.y = SCREEN_MAIN_HEIGHT
            } else if (direction == GestureDirection.DOWN) {
                height = 200 - (rawDistance * 2000f).toInt()
                dimens.x = height
                dimens.y = 0

            }
        }else if (state == SceneState.FOCUSED) {
                    if (direction == GestureDirection.UP) {
                        height = (1200 - (rawDistance * 5000)).toInt()
                        dimens.x = height
                        dimens.y = 200
                    } else if (direction == GestureDirection.DOWN) {
                        height = (SCREEN_MAIN_HEIGHT - (rawDistance * 10000)).toInt()
                        dimens.x = height
                        dimens.y = 200
                    }

                    Log.d("SceneManager||filter : ", "$state and Height $height and Dimensions $dimens")
        }else if (state == SceneState.INDEXED) {
            if (direction == GestureDirection.UP) {
                height = 200
                dimens.x = height
                dimens.y = 200
            } else if (direction == GestureDirection.DOWN) {
                height = ((rawDistance * 10000) + 200).toInt()
                dimens.x = height
                dimens.y = SCREEN_MAIN_HEIGHT
            }
        }else if (state == SceneState.ARCHIVED) {
                height = 200
                dimens.x = height
                dimens.y = 200
        }

        return dimens
    }
    fun getStateSpecs(state: SceneState, direction: GestureDirection):StateSpecs{
        var startingY = 0
        var startingX = 0
        var endingX = 0
        var endingY = 0
        var startingHeaderTextSize = 0f
        var startingHeaderTextAlpha = 0f
        var startingBackgroundColor = 0
        var endingHeaderTextSize = 0f
        var endingHeaderTextAlpha = 0f
        var endingBackgroundColor = 0

        if (state == SceneState.STAGED) {
            if (direction == GestureDirection.UP){
                startingY = 200
                endingY = SCREEN_MAIN_HEIGHT
                startingX = MATCH_PARENT
                endingX = MATCH_PARENT // match screen width

            }else if(direction == GestureDirection.DOWN){
                startingY = 200
                endingY = 0
                startingX = MATCH_PARENT
                endingX = 0
            }
            startingHeaderTextSize = LARGE_TEXT
            endingHeaderTextSize = MEDIUM_TEXT
            startingHeaderTextAlpha = 0.5f
            endingHeaderTextAlpha = 1f
            startingBackgroundColor = Color.TRANSPARENT
            endingBackgroundColor = Color.TRANSPARENT
        }
        else if (state == SceneState.FOCUSED) {
            if (direction == GestureDirection.UP){
                startingY = SCREEN_MAIN_HEIGHT
                endingY = 200
                startingX = MATCH_PARENT
                endingX = 600
                startingHeaderTextSize = MEDIUM_TEXT
                endingHeaderTextSize = SMALL_TEXT
                startingHeaderTextAlpha = 1f
                endingHeaderTextAlpha = .75f
                startingBackgroundColor = Color.TRANSPARENT
                endingBackgroundColor = Color.BLACK
            }else if(direction == GestureDirection.DOWN){
                startingY = SCREEN_MAIN_HEIGHT
                endingY = 200
                startingX = MATCH_PARENT
                endingX = MATCH_PARENT
                startingHeaderTextSize = MEDIUM_TEXT
                endingHeaderTextSize = LARGE_TEXT
                startingHeaderTextAlpha = 1f
                endingHeaderTextAlpha = 0.5f
                startingBackgroundColor = Color.TRANSPARENT
                endingBackgroundColor = Color.TRANSPARENT
            }

        }
        else if (state == SceneState.INDEXED) {
            if (direction == GestureDirection.UP){
                startingY = 200
                endingY = 200
                startingX = 600
                endingX = 600
                startingHeaderTextSize = SMALL_TEXT
                endingHeaderTextSize = SMALL_TEXT
                startingHeaderTextAlpha = .75f
                endingHeaderTextAlpha = .75f
                startingBackgroundColor = Color.BLACK
                endingBackgroundColor = Color.BLACK

            }else if(direction == GestureDirection.DOWN){
                startingY = 200
                endingY = SCREEN_MAIN_HEIGHT
                startingX = 600
                endingX = MATCH_PARENT
                startingHeaderTextSize = SMALL_TEXT
                endingHeaderTextSize = MEDIUM_TEXT
                startingHeaderTextAlpha = .75f
                endingHeaderTextAlpha = 1f
                startingBackgroundColor = Color.BLACK
                endingBackgroundColor = Color.TRANSPARENT
            }
        }else if (state == SceneState.HIDDEN) {
            if (direction == GestureDirection.UP){
                startingY = 0
                endingY = 200
                startingX = 0
                endingX = MATCH_PARENT

            }else if(direction == GestureDirection.DOWN){
                startingY = 0
                endingY = 0
                startingX = 0
                endingX = 0
            }
            startingHeaderTextSize = SMALL_TEXT
            endingHeaderTextSize = LARGE_TEXT
            startingHeaderTextAlpha = 0.0f
            endingHeaderTextAlpha = 0.5f
            startingBackgroundColor = Color.TRANSPARENT
            endingBackgroundColor = Color.TRANSPARENT
        }
        return StateSpecs(state,direction,startingX,endingX,startingY,endingY,startingHeaderTextSize, endingHeaderTextSize, startingHeaderTextAlpha, endingHeaderTextAlpha, startingBackgroundColor, endingBackgroundColor)
    }

    fun getEndingHeight(): Float {
        return height.toFloat()
    }

    fun getSceneHeight(state: SceneState): Float {
        return when (state) {
            SceneState.FOCUSED -> SCREEN_MAIN_HEIGHT.toFloat()
            SceneState.WAITING, SceneState.HIDDEN -> SCREEN_ZERO_HEIGHT
            else -> SCREEN_QUARTER_HEIGHT
        }
    }
    fun getInitialHeight(state: SceneState): Float {
        return when (state) {
            SceneState.STAGED -> SCREEN_QUARTER_HEIGHT
            SceneState.FOCUSED -> SCREEN_MAIN_HEIGHT.toFloat()
            SceneState.INDEXED -> SCREEN_QUARTER_HEIGHT
            else -> SCREEN_ZERO_HEIGHT
        }
    }

    fun isOverThreshold(currentViewHeight: Float, state: SceneState, direction: GestureDirection): Boolean {
        return when (state) {
            // passed threshold when current view height is less than half main height (focused height)
            SceneState.FOCUSED -> currentViewHeight < SCREEN_MAIN_HEIGHT/2
            // ...is greater than half main height.
            SceneState.STAGED -> {
                if(direction == GestureDirection.UP) currentViewHeight > SCREEN_MAIN_HEIGHT/2
                else currentViewHeight < SCREEN_QUARTER_HEIGHT/2
            }

            SceneState.INDEXED -> {
                if(direction == GestureDirection.DOWN) currentViewHeight > SCREEN_MAIN_HEIGHT/2
                else true
            }
            SceneState.HIDDEN -> currentViewHeight > SCREEN_QUARTER_HEIGHT/2
            else -> false
        }
    }
}
// TextSize, TextColor, TextAlpha, Background Color.

data class Dimens(val state: SceneState,val direction: GestureDirection,val startingX : Int, val endingX : Int, val startingY : Int, val endingY : Int)
data class StateSpecs(val state: SceneState, val direction: GestureDirection, val startingX : Int, val endingX : Int, val startingY : Int, val endingY : Int, val startingHeaderTextSize : Float,
                      val endingHeaderTextSize : Float, val startingHeaderTextAlpha : Float, val endingHeaderTextAlpha : Float, val startingBackgroundColor : Int, val endingBackgroundColor : Int)