package com.dmonk.mara.Utils.layoutTransition.Scene

import com.dmonk.mara.Utils.layoutTransition.Scene.Animations.StateInterface

enum class SceneState: StateInterface {
    WAITING {
            override fun getNext() = HIDDEN
            override fun getPrevious() = WAITING
            },
    HIDDEN {
        override fun getNext() = STAGED
        override fun getPrevious() = WAITING

    },
    STAGED {
        override fun getNext() = FOCUSED
        override fun getPrevious() = HIDDEN

    },
    FOCUSED {
        override fun getNext() = INDEXED
        override fun getPrevious() = STAGED

    },
    INDEXED {
        override fun getNext() = ARCHIVED
        override fun getPrevious() = FOCUSED

    },
    ARCHIVED {
        override fun getNext() = ARCHIVED
        override fun getPrevious() = INDEXED

    },

}
