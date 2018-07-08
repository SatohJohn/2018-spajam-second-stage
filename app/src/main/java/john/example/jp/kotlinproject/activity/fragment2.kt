package john.example.jp.kotlinproject.activity


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import john.example.jp.kotlinproject.R


/**
 * A simple [Fragment] subclass.
 */
class Fragment2 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment2, container, false)
    }

}
