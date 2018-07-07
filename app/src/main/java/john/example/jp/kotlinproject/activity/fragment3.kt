package john.example.jp.kotlinproject.activity


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import john.example.jp.kotlinproject.CameraActivity
import john.example.jp.kotlinproject.MainActivity
import john.example.jp.kotlinproject.R
import kotlinx.android.synthetic.main.fragment3.*


/**
 * A simple [Fragment] subclass.
 */
class Fragment3 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment3, container, false)

    }



}
