package com.dmonk.mara

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class FragmentSelectionModel : ViewModel() {

    // Tracks fragment selection
    public final lateinit var fragmentSelectionModel: LiveData<Int>



}