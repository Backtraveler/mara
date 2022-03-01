package com.dmonk.mara.Utils.layoutTransition.Scene

import android.view.View
import com.dmonk.mara.Utils.layoutTransition.GestureDirection

interface Controller : View.OnClickListener {
    fun onFling(direction: GestureDirection)
    fun onScroll(direction: GestureDirection, distance : Float)
    fun onReset()
}