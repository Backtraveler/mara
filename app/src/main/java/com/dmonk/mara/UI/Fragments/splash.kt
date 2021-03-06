@file:JvmName("SplashKt")

package com.dmonk.mara.UI.Fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import androidx.fragment.app.activityViewModels
import com.dmonk.mara.R
import com.dmonk.mara.UI.Activity.GettingStarted
import com.dmonk.mara.UI.Activity.Login
import com.dmonk.mara.Utils.GestureListener
import com.dmonk.mara.ViewModels.TouchEventModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Flash.newInstance] factory method to
 * create an instance of this fragment.
 */
class Flash : Fragment(), View.OnClickListener, View.OnTouchListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var login: Button;
    private lateinit var gettingStarted: Button;

    // Gesture detector
    private lateinit var detector: GestureDetector;

    // Gesture viewModel
    private val touchEventModel: TouchEventModel by activityViewModels()

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
        var view:View = inflater.inflate(R.layout.fragment_flash, container, false)

        login = view.findViewById(R.id.login)
        gettingStarted = view.findViewById(R.id.get_started)

        login.setOnClickListener(this)
        gettingStarted.setOnClickListener(this)

        // init gesture detector
        detector = GestureDetector(context, GestureListener())
        detector.setIsLongpressEnabled(false)


        // set view touch listener
        view.setOnTouchListener(this)

        return view;
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Flash.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Flash().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(p0: View?) {
        var intent: Intent? = null

        when(p0?.id){
            R.id.login -> intent = Intent(context, Login::class.java)
            R.id.get_started -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent = Intent(context, GettingStarted::class.java)
            }
        }
        startActivity(intent)

    }

    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        // Handle touch events through detector.
        detector.onTouchEvent(p1)
        return true
    }


}