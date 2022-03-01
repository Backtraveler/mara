package com.dmonk.mara.UI.Fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.dmonk.mara.R
import com.dmonk.mara.Utils.FragmentTransitionListener
import kotlin.math.abs


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [test.newInstance] factory method to
 * create an instance of this fragment.
 */
class test : Fragment(), FragmentTransitionListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var header : TextView
    private val DEFAULT_TEXT_SIZE = 72f
    private var start = DEFAULT_TEXT_SIZE
    private lateinit var headerText : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_test, container, false)
        header = view.findViewById(R.id.header)
        header.text = headerText

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment test.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            test().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }

    fun setHeader(value: String){
        // Initial value
        this.headerText = value
    }
    fun resetHeader(value: String){
        this.header.text = value
    }


    override fun beginFragmentAnimation(startId: Int, endId: Int) {

        Log.d("TestFragment","Fragment animation is starting...$startId...$endId")

    }

    override fun expandFragment() {
        val animator = ValueAnimator.ofFloat(start, DEFAULT_TEXT_SIZE)
        animator.duration = 500L

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            header.textSize = animatedValue
        }

        animator.start()      }

    override fun shrinkFragment() {
         }

    override fun progressAnimationValue( value: Float) {



        var end = (abs(value) * DEFAULT_TEXT_SIZE)
        if (end > DEFAULT_TEXT_SIZE) end = DEFAULT_TEXT_SIZE



        val animator = ValueAnimator.ofFloat(start, end)
        animator.duration = 10L

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            header.textSize = animatedValue
            R.dimen.large_text
        }

        animator.start()
        start = end

    }

    fun getHeaderView() : TextView = header


}