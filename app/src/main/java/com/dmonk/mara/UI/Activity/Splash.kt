package com.dmonk.mara.UI.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.dmonk.mara.R
import com.dmonk.mara.ViewModels.TouchEventModel
import com.dmonk.mara.ui.login.LoginFragment


class Splash : AppCompatActivity(){

    // Gesture viewModel
    private val touchEventModel: TouchEventModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Get swipe direction
        touchEventModel.vertical.observe(this){
            direction -> Log.d("GestureListener:","$direction Motion handled by main")

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace<LoginFragment>(R.id.fragmentContainerView)
                addToBackStack("Login")
            }
        }


    }




}