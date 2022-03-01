package com.dmonk.mara.Utils.layoutTransition.Scene

import com.dmonk.mara.Utils.layoutTransition.GestureDirection
import com.dmonk.mara.Utils.layoutTransition.GestureEvent
import com.dmonk.mara.Utils.layoutTransition.GestureType
import org.junit.Assert.*

import junit.framework.TestCase

import org.junit.Before
import org.junit.Test
import java.util.*

class SceneManagerTest : TestCase() {

    lateinit var sceneManager: SceneManager
    val firstSceneState = SceneState.FOCUSED

    @Before
    public override fun setUp() {
       val list = Stack<Scene>()
       for (i in 0..3)
          list.add(Scene())
       sceneManager = SceneManager(list,firstSceneState)
    }

    @Test
    fun testActive(){
        val first = sceneManager.getActiveScenes().first()
        assertEquals(3,sceneManager.getActiveScenes().size)
        assertEquals(firstSceneState,first.getState())
        assertEquals(4,first.id)
    }

    @Test
    fun testArchived(){
        // When first scene is focused or Indexed, archived list should be empty.
        assertEquals(0,sceneManager.getArchivedScenes().size)
    }
    @Test
    fun testWaiting(){
        assertEquals(1,sceneManager.getWaitingScenes().size)
    }
    @Test
    fun testListAfterScroll(){
        // Create gesture
        var gestureEvent = GestureEvent(GestureType.SCROLL,GestureDirection.UP,50f)
        sceneManager.gestureEventListener(gestureEvent)

        assertEquals(0,sceneManager.getWaitingScenes().size)
        assertEquals(0,sceneManager.getPendingInclusion().size)
        assertEquals(SceneState.FOCUSED,sceneManager.getActiveScenes().first().getState())

        gestureEvent = GestureEvent(GestureType.SCROLL,GestureDirection.UP,50f)
        sceneManager.gestureEventListener(gestureEvent)

        assertEquals(0,sceneManager.getWaitingScenes().size)
        assertEquals(0,sceneManager.getPendingInclusion().size)
        assertEquals(SceneState.INDEXED,sceneManager.getActiveScenes().first().getState())

    }
    @Test
    fun testListsAfterFling(){
        // Create gesture
        var gestureEvent = GestureEvent(GestureType.FLING,GestureDirection.UP)
        sceneManager.gestureEventListener(gestureEvent)

        assertEquals(0,sceneManager.getWaitingScenes().size)
        assertEquals(0,sceneManager.getPendingInclusion().size)
        assertEquals(SceneState.INDEXED,sceneManager.getActiveScenes().first().getState())

        // Create gesture
        gestureEvent = GestureEvent(GestureType.FLING,GestureDirection.UP)
        sceneManager.gestureEventListener(gestureEvent)

        assertEquals(0,sceneManager.getWaitingScenes().size)
        assertEquals(1,sceneManager.getPendingExclusion().size)
        assertEquals(1,sceneManager.getArchivedScenes().size)

        // Create gesture
        gestureEvent = GestureEvent(GestureType.FLING,GestureDirection.UP)
        sceneManager.gestureEventListener(gestureEvent)

        assertEquals(0,sceneManager.getWaitingScenes().size)
        assertEquals(1,sceneManager.getPendingExclusion().size)
        assertEquals(2,sceneManager.getArchivedScenes().size)

        // Create gesture
        gestureEvent = GestureEvent(GestureType.FLING,GestureDirection.UP)
        sceneManager.gestureEventListener(gestureEvent)

        assertEquals(0,sceneManager.getWaitingScenes().size)
        assertEquals(1,sceneManager.getPendingExclusion().size)
        assertEquals(3,sceneManager.getArchivedScenes().size)

        // Create gesture
        gestureEvent = GestureEvent(GestureType.FLING,GestureDirection.DOWN)
        sceneManager.gestureEventListener(gestureEvent)

        assertEquals(0,sceneManager.getWaitingScenes().size)
        assertEquals(0,sceneManager.getPendingExclusion().size)
        assertEquals(2,sceneManager.getArchivedScenes().size)

    }
}