package com.dmonk.mara.Utils.layoutTransition.Scene.Animations

import com.dmonk.mara.Utils.layoutTransition.Scene.SceneState

interface StateInterface {
    fun getNext():SceneState
    fun getPrevious():SceneState
}