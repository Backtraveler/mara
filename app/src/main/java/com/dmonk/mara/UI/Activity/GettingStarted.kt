package com.dmonk.mara.UI.Activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.dmonk.mara.R
import com.dmonk.mara.UI.Fragments.test
import com.dmonk.mara.Utils.layoutTransition.GestureBuffer
import com.dmonk.mara.Utils.layoutTransition.GestureListener
import com.dmonk.mara.Utils.layoutTransition.Scene.Scene
import com.dmonk.mara.Utils.layoutTransition.Scene.SceneManager
import com.dmonk.mara.Utils.layoutTransition.Scene.SceneState
import java.util.*


@RequiresApi(Build.VERSION_CODES.M)
class GettingStarted : AppCompatActivity(), View.OnTouchListener{
    lateinit var testFragment: test
    lateinit var testFragment2: test
    // Gesture detector
    private lateinit var detector: GestureDetector;
    var start =  0f;
    var end = 0f

    lateinit var contentView: ConstraintLayout
    lateinit var linearLayout: LinearLayout

    var focusA : View? = null
    var focusB : View? = null
    var focusC : View? = null

    val scenes = Stack<Scene>()
    lateinit var sceneManager: SceneManager
    var simValue = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.slidinglayout)


        contentView = findViewById(R.id.content)
        contentView.setOnTouchListener(this)
        linearLayout = findViewById(R.id.layout)


            scenes.add(Scene(findViewById(R.id.frame4),supportFragmentManager))
            scenes.add(Scene(findViewById(R.id.frame3),supportFragmentManager))
            scenes.add(Scene(findViewById(R.id.frame2),supportFragmentManager))
            scenes.add(Scene(findViewById(R.id.frame1),supportFragmentManager))

        sceneManager = SceneManager(linearLayout, scenes, SceneState.FOCUSED)



        Log.d("GettingStarted: "," Number of children ${linearLayout.childCount}")
        Log.d("GettingStarted: ","Test")

        // init gesture detector
        detector = GestureDetector(this, GestureListener(sceneManager.getGestureBuffer()))
        detector.setIsLongpressEnabled(false)


    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

        if (p1!!.getAction() == MotionEvent.ACTION_UP){

            sceneManager.getGestureBuffer().onGestureInterrupted()
            // reset motion value
            //simValue = 0f

        }else {

            // Handle touch events through detector.
        }
        detector.onTouchEvent(p1)

        return true
    }



}