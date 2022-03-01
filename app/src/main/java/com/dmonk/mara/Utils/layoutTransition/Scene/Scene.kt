package com.dmonk.mara.Utils.layoutTransition.Scene

import android.animation.Animator
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.dmonk.mara.UI.Fragments.test
import com.dmonk.mara.Utils.layoutTransition.GestureDirection
import com.dmonk.mara.Utils.layoutTransition.Scene.Animations.SceneAnimator
import com.dmonk.mara.Utils.layoutTransition.Scene.Animations.TransitionAnimationListener
import org.jetbrains.annotations.NotNull

class Scene(private val sceneContainer: FrameLayout, val fragmentManager: FragmentManager) : Controller, TransitionAnimationListener() {
    private lateinit var sceneFragment: test
    companion object{
        var number = 0
    }
    var animationCompleteCounter = 0
    var id = ++number
    private lateinit var listener: SceneTransformationListener
    private lateinit var direction: GestureDirection
    private var transitionPercentCompleted = 0

    // view bounds set by state.
    private var currentViewHeight = 0f

    // Transition vars.
    private val sceneAnimator = SceneAnimator()
    private val filter = StateFilter()


    private lateinit var state: SceneState
    private val sceneHeader: TextView by lazy { sceneFragment.getHeaderView() }

    init {
        val fragmentId = sceneContainer.getChildAt(0).id
        sceneFragment = fragmentManager.findFragmentById(fragmentId) as test
        direction = GestureDirection.UP
    }

    fun setState(state: SceneState){
        this.state = state
        currentViewHeight = filter.getInitialHeight(state)
        sceneFragment.setHeader(state.name)


    }

    fun getState():SceneState = state

    fun updateState(){
        // Change state based on direction
        state = if(direction == GestureDirection.UP) {
            state.getNext()
        }else{
            state.getPrevious()
        }
        Log.d("Scene:","New State: $state.")


        // Rest header
        sceneFragment.resetHeader(state.name)
        // Reset initial height
        currentViewHeight = filter.getSceneHeight(state)

        // Set onclick listener
        if(state == SceneState.ARCHIVED || state == SceneState.INDEXED)
            sceneHeader.setOnClickListener(this)
        else // remove listener
            sceneHeader.setOnClickListener(null)

    }
    fun updateState(direction : GestureDirection){
        this.direction = direction
        // Change state based on direction
        state = if(direction == GestureDirection.UP) {
            state.getNext()
        }else{
            state.getPrevious()
        }
        Log.d("Scene:","New State: $state.")


        // Rest header
        sceneFragment.resetHeader(state.name)
        // Reset initial height
        currentViewHeight = filter.getSceneHeight(state)

        // Set onclick listener
        if(state == SceneState.ARCHIVED || state == SceneState.INDEXED)
            sceneHeader.setOnClickListener(this)
        else // remove listener
            sceneHeader.setOnClickListener(null)

    }

    /**
     * Delegates animation based on fling direction.
     * @param direction GestureDirection
     */
    override fun onFling(direction: GestureDirection) {
        // set direction
        this.direction = direction


        // Animate Scene Container
        // Non animating states.
            if (state == SceneState.WAITING || state == SceneState.ARCHIVED) {
                onAnimationEnd(null)

            } else {
                // Animate based in fling direction.
                // Get specs based on state and direction.
                val stateSpecs = filter.getStateSpecs(state, direction)


                // Reset filter
                filter.reset()

                sceneAnimator.animateFling(
                    sceneContainer,
                    sceneHeader,
                    stateSpecs,
                    this
                )
            }

        // Animate Scene Contents.
        // TextSize, TextColor, TextAlpha, Background Color.





    }

    override fun onScroll(direction: GestureDirection, distance: Float) {
        this.direction = direction


        // Get filtered height (X) and height limit (Y)
        val filteredRange = filter.getFilteredRange(direction, distance,state)
        Log.d("Scene:","State : $state || Direction : $direction ||  RawDistance $distance || Starting height : ${filteredRange.x} || height Limit : ${filteredRange.y}")

        // Animate to new height
        sceneAnimator.animateScroll(sceneContainer,filteredRange.x.toFloat(),filteredRange.y.toFloat(),this)


    }

    override fun onReset() {
        Log.d("Scene:","On reset called for $state")
        if(isTransitionNearComplete()){
            // Complete transition
            Log.d("Scene:","Above threshold...............")

            // Animate based in fling direction.
            val filteredRange = filter.getStateSpecs(state, direction)
            val start = if(filter.getEndingHeight() == 0f) filteredRange.startingY.toFloat() else currentViewHeight
            val end = filteredRange.endingY.toFloat()

            // Reset filter
            filter.reset()
            sceneAnimator.animateFling(
                sceneContainer,
                start,
                end,
                this
            )
        }else {
            Log.d("Scene:","Below threshold...............")
            // Return to starting state (Keep state)

            val start = currentViewHeight
            val end = filter.getSceneHeight(state)
            sceneAnimator.animateFling(
                sceneContainer,
                start,
                end
            )
        }
    }

    override fun onClick(p0: View?) {
        Log.d("Scene:","Successful onclick called XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
        listener.onSceneSelection(this)
    }

    private fun isTransitionNearComplete(): Boolean {
        //No Scroll events recorded.
        if(currentViewHeight == 0f) return false
        return filter.isOverThreshold(currentViewHeight,state,direction)
    }


    fun addStateChangeListener(listener: SceneTransformationListener) {
        this.listener = listener
    }

    private fun onSceneTransitionCompleted(){
        Log.d("Scene : ", "Transition Completed....................................................................................$state")

        // Notify SceneManager of scene transition

            listener.onSceneTransitionComplete(this)



    }

    override fun onContentViewResized(newHeight: Float, newWidth: Float) {
        // Save new height
        currentViewHeight = newHeight
        // When scrolling without full transition. Otherwise calls transition completed.
        listener.onSceneResized(this)
    }
    override fun onAnimationEnd(p0: Animator?) {

        ++animationCompleteCounter
        Log.d("Scene:","Animation for $state complete called $animationCompleteCounter times.")

            onSceneTransitionCompleted()

    }

}