package john.example.jp.kotlinproject.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraMetadata
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.transition.Visibility
import android.view.View
import john.example.jp.kotlinproject.R
import john.example.jp.kotlinproject.data.UseCameraData

class MainActivity : AppCompatActivity() {

    var pager: ViewPager? = null

    var adapter: FragmentPagerAdapter? = null

    var currentPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        pager =  findViewById(R.id.pager)

        adapter = UserInfoViewPagerAdapter(getSupportFragmentManager());
        pager?.setAdapter(adapter);
        currentPage = 0;
        button.setOnClickListener {

            // 新しく開くアクティビティに渡す値
//            val intent: Intent = Intent(this, GPSActivity::class.java)
//            intent.putExtra("number", 120)
//            intent.putExtra("string", "The message from MainActivity")
//
//            // 新しくアクティビティを開く
//            startActivityForResult(intent, MY_REQUEST_CODE)

            // TODO:CameraVideoFragmentを呼ぶ前にカメラを設定
            UseCameraData.useCameraKind = CameraMetadata.LENS_FACING_BACK

            savedInstanceState ?: supportFragmentManager.beginTransaction()
                    .replace(R.id.container, CameraVideoFragment.newInstance())
                    .commit()

            button.visibility = View.INVISIBLE

        }

        debugButton.setOnClickListener {
            Log.i(this::class.java.simpleName, "hi debug mode!")
            // 新しくアクティビティを開く
            val intent: Intent = Intent(this, DebugActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        button.visibility = View.VISIBLE
        super.onResume()
    }

    private val RECORD_REQUEST_CODE = 101

    private fun setupPermissions() {
        val permissionGrantedList: List<String> = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .filter {
                    PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, it)
                }
        if (permissionGrantedList.isNotEmpty()) {
            Log.i(this::class.java.simpleName, "permission set up: ${permissionGrantedList}")
            ActivityCompat.requestPermissions(this, permissionGrantedList.toTypedArray(), RECORD_REQUEST_CODE)
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
