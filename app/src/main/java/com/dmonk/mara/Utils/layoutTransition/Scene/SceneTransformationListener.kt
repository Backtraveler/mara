package com.dmonk.mara.Utils.layoutTransition.Scene

/**
 * Helps SceneManager delegate gesture values and move scenes between lists.
 */
interface SceneTransformationListener {
    fun onSceneResized(scene: Scene)
    fun onSceneTransitionComplete(scene : Scene)
    fun onSceneSelection(scene: Scene)
}