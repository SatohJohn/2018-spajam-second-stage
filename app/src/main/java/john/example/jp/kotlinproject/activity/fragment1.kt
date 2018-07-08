package john.example.jp.kotlinproject.activity


import android.hardware.camera2.CameraMetadata
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import john.example.jp.kotlinproject.R
import john.example.jp.kotlinproject.data.UseCameraData
import kotlinx.android.synthetic.main.activity_camera.*


/**
 * A simple [Fragment] subclass.
 */
class Fragment1 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment1, container, false)
    }

}
