package com.dmonk.mara.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SceneSelectionModel : ViewModel() {

    private val mutableSelectedScene = MutableLiveData<Int>()
    val selectedScene: LiveData<Int> get() = mutableSelectedScene
    fun selectScene(track: Int){
        mutableSelectedScene.value = track
    }



}