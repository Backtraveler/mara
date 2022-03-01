package com.dmonk.mara.ViewModels

import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dmonk.mara.Utils.SwipeDirection

class TouchEventModel : ViewModel(){

    private val mutableDirection = MutableLiveData<SwipeDirection>()
    private val mutableMotionEvent = MutableLiveData<MotionEvent>()
    private val mutableVerticalEvent = MutableLiveData<SwipeDirection>()
    private val mutableHorizontalEvent = MutableLiveData<SwipeDirection>()
    private val mutableMotionResult = MutableLiveData<Float>()

    val direction: LiveData<SwipeDirection> get() = mutableDirection
    val motionEvent: LiveData<MotionEvent> get() = mutableMotionEvent
    val vertical: LiveData<SwipeDirection> get() = mutableVerticalEvent
    val horizontal: LiveData<SwipeDirection> get() = mutableHorizontalEvent
    val motionResult: LiveData<Float> get() = motionResult

    fun setMotionResult(value: Float){
        mutableMotionResult.value = value
    }

    fun setDirection(direction: SwipeDirection){
        mutableDirection.value = direction
    }
    fun touched(motionEvent: MotionEvent){
        mutableMotionEvent.value = motionEvent
    }

    fun verticalSwipe(direction: SwipeDirection){
        mutableVerticalEvent.value = direction

    }

    fun horizontalSwipe(direction: SwipeDirection){
        mutableHorizontalEvent.value = direction
    }
}