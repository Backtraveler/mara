package com.dmonk.mara.Utils

interface FragmentTransitionListener {
    fun beginFragmentAnimation(startId: Int, endId: Int)
    fun expandFragment()
    fun shrinkFragment()
    fun progressAnimationValue( value: Float)
}