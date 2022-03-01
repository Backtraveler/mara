package com.dmonk.mara.Utils.layoutTransition.Scene

import android.util.Log
import android.widget.LinearLayout
import com.dmonk.mara.Utils.layoutTransition.*
import java.util.*

class SceneManager(val layout: LinearLayout, sceneList: Stack<Scene>, firstSceneState: SceneState) : SceneTransformationListener, BufferCallback{
    private val activeScenes = mutableListOf<Scene>()
    private val allScenes = mutableListOf<Scene>()
    private val archivedScenes = Stack<Scene>()
    private val waitingScenes = Stack<Scene>()
    private val pendingInclusion = Stack<Scene>()
    private val pendingExclusion = Stack<Scene>()

    // Scene transition vars.
    private var afterTransition = true
    private var numberOfActiveScenes = 0
    private var onSceneCompleteCount = 0

    // Gesture vars.
    private val buffer = GestureBuffer(this,layout.context)
    private var gestureCount = 0
    private var gestureInterrupted = false

    init {
        // Populate activeScenes list and set scene states based on first state.

        // Set to prior state to enable seamless for loop addition.
        var state = firstSceneState.getNext()
        while (sceneList.isNotEmpty()){
            var scene = sceneList.pop()
            // Add tp master list
            allScenes.add(scene)
            // Set state to next
            state = state.getPrevious()
            scene.setState(state)
            scene.addStateChangeListener(this)

            // Determine list based on state
            when(state){

                SceneState.WAITING -> {
                    if(pendingInclusion.isEmpty())
                        pendingInclusion.push(scene)
                    else
                        waitingScenes.push(scene)
                }

                SceneState.ARCHIVED -> {
                    if(pendingInclusion.isEmpty())
                        pendingInclusion.push(scene)
                    else
                        archivedScenes.push(scene)
                }

                else -> {activeScenes.add(scene)}
            }



        }

    }



    fun getGestureBuffer():GestureBuffer = buffer
    fun getActiveScenes():List<Scene> = activeScenes
    fun getArchivedScenes():Stack<Scene> = archivedScenes
    fun getWaitingScenes():Stack<Scene> = waitingScenes
    fun getPendingInclusion():Stack<Scene> = pendingInclusion
    fun getPendingExclusion():Stack<Scene> = pendingExclusion

    override fun onNextGestureEvent(gestureEvent: GestureEvent){

        // Update active scenes with archived and waiting
        updateActiveScenes(gestureEvent.direction)
            // Filter gesture
            if (gestureEvent.type == GestureType.SCROLL) {
                logList()

                // Pass gesture values to activeScenes
                for (scene in activeScenes)
                   scene.onScroll(gestureEvent.direction, gestureEvent.distance)

            } else
                if (gestureEvent.type == GestureType.FLING) {

                    // Pass gesture values to activeScenes
                    for (scene in activeScenes)
                        scene.onFling(gestureEvent.direction)

                }

            // Refresh layout
            layout.requestLayout()


    }

    override fun onIncompleteGestureEvent() {
        // Reset scenes
        for (scene in activeScenes)
            scene.onReset()
    }
    private fun logList(){
        Log.d("SceneManager: ","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
        Log.d("SceneManager: ","Number of archived scenes ${archivedScenes.size}")
        Log.d("SceneManager: ","Number of waiting scenes ${waitingScenes.size}")
        Log.d("SceneManager: ","Number of active scenes ${activeScenes.size}")
        Log.d("SceneManager: ","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
    }

    /**
     * Updates Active scenes List based on fling events.
     */
    private fun updateActiveScenes(direction: GestureDirection) {

        if(afterTransition) {

            // Select origin for scene inclusion
            if (direction == GestureDirection.UP && waitingScenes.isNotEmpty()) {
                // Grab scene from waiting
                pendingInclusion.add(waitingScenes.pop())
            } else if (direction == GestureDirection.DOWN && archivedScenes.isNotEmpty()) {
                // Grab scene from archived
                pendingInclusion.add(archivedScenes.pop())
            }

            //Check pending
            //Exclude before including caution against same object exclusion.
            if (pendingExclusion.isNotEmpty())
                activeScenes.remove(pendingExclusion.pop())
            if (pendingInclusion.isNotEmpty())
                activeScenes.add(pendingInclusion.pop())

            // Set number of active scenes
            numberOfActiveScenes = activeScenes.size

            // Reset transition flag.
            afterTransition = false
        }

    }


    private fun onArchiveStateChange(scene: Scene) {

        // Archive and exclude
        archivedScenes.push(scene)
        pendingExclusion.push(scene)
    }

    private fun onWaitingStateChange(scene: Scene) {

        // Wait and exclude
        waitingScenes.push(scene)
        pendingExclusion.push(scene)    }

    override fun onSceneResized(scene: Scene) {
       // Decrement number of active scenes
        if(--numberOfActiveScenes == 0){
            // Set number of active scenes
            numberOfActiveScenes = activeScenes.size
            buffer.getNextGestureEvent()
        }

    }

    override fun onSceneTransitionComplete(scene: Scene) {
        // Possible trigger for other events.
        Log.d("SceneManager: ","_________________________________________________")
        Log.d("SceneManager: ","${scene.getState()} Completed transition. Number of Active Scenes $numberOfActiveScenes")

        if(--numberOfActiveScenes == 0) {

            // Update scene states
            for (scene in activeScenes)
                scene.updateState()


            // Complete layout transition.
            layoutTransitionComplete()
        }

    }

    override fun onSceneSelection(scene: Scene) {
        // Get position of scene in master list.
        var scenePosition = allScenes.indexOf(scene)

        // loop starting index
        val sIndex = if (scenePosition == 0) scenePosition else --scenePosition
        // Update state to near focus (index) final transition to be handled by synthetic onFling call.
        while (scene.getState() != SceneState.INDEXED){
            // Update every scene from current scene plus 1.
            for (i in allScenes.size-1 downTo sIndex){
                // When archived check for indexed, if yes end loop.
                if (allScenes[i].getState() == SceneState.ARCHIVED && allScenes[i+1].getState() == SceneState.INDEXED) break
                // Update state
                allScenes[i].updateState(GestureDirection.DOWN)
            }
        }

        // Rebuild lists.
        activeScenes.clear()
        waitingScenes.clear()
        archivedScenes.clear()
        pendingInclusion.clear()
        pendingExclusion.clear()

        for (s in allScenes) {
            // Determine list based on state
            when (s.getState()) {

                SceneState.WAITING -> {
                    if (pendingInclusion.isEmpty())
                        pendingInclusion.push(s)
                    else
                        waitingScenes.push(s)
                }

                SceneState.ARCHIVED -> {
                    if (pendingInclusion.isEmpty())
                        pendingInclusion.push(s)
                    else
                        archivedScenes.push(s)
                }

                else -> {
                    activeScenes.add(s)
                }
            }
        }


        // Set after transition flag
        afterTransition = true

        // Synthetic gesture
        onNextGestureEvent(GestureEvent(GestureType.FLING,GestureDirection.DOWN))



    }


    private fun layoutTransitionComplete() {

        Log.d("SceneManager: ","XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX Transition Completed XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")

        // Re-organize lists.
        for (scene in activeScenes){
            if (scene.getState() == SceneState.ARCHIVED  && !archivedScenes.contains(scene)) onArchiveStateChange(scene)
            else if (scene.getState() == SceneState.WAITING && !waitingScenes.contains(scene)) onWaitingStateChange(scene)
        }

        // set transition complete flag
        afterTransition = true

        // Get next gesture from buffer.
        buffer.getNextGestureEvent()


    }


}