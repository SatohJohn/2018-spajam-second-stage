package john.example.jp.kotlinproject.activity


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.appcompat.R.id.checkbox
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import john.example.jp.kotlinproject.R
import kotlinx.android.synthetic.main.activity_main.*


/**
 * A simple [Fragment] subclass.
 */
class Fragment1 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment1, container, false)
    }

}
