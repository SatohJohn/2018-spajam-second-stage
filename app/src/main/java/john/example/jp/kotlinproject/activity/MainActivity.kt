package john.example.jp.kotlinproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import john.example.jp.kotlinproject.R
import kotlinx.android.synthetic.main.activity_main.*

const val MY_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button.setOnClickListener {

            // 新しく開くアクティビティに渡す値
            val intent: Intent = Intent(this, CameraActivity::class.java)
            intent.putExtra("number", 120)
            intent.putExtra("string", "The message from MainActivity")

            // 新しくアクティビティを開く
            startActivityForResult(intent, MY_REQUEST_CODE)
        }
    }

    private val RECORD_REQUEST_CODE = 101

    private fun setupPermissions() {
        val permissionGrantedList: List<String> = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET,
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
