package john.example.jp.kotlinproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.AlphabeticIndex
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceView
import android.widget.Button
import john.example.jp.kotlinproject.activity.GPSActivity
import kotlin.math.max

const val MY_REQUEST_CODE = 0

class MainActivity : AppCompatActivity() {

    var _record:Record? = null
    var _isRecording = false
    var _button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupPermissions()

        button.setOnClickListener {

            // 新しく開くアクティビティに渡す値
            val intent: Intent = Intent(this, GPSActivity::class.java)
//            intent.putExtra("number", 120)
//            intent.putExtra("string", "The message from MainActivity")
//
//            // 新しくアクティビティを開く
            startActivityForResult(intent, MY_REQUEST_CODE)
//            if(_isRecording)
//                stopRecord()
//            else
//                doRecord()
//            savedInstanceState ?: supportFragmentManager.beginTransaction()
//                    .replace(R.id.container, CameraVideoFragment.newInstance())
//                    .commit()

        }

    }
    override fun onBackPressed() {
        super.onBackPressed()
        stopRecord()
    }

    override fun onPause() {
        super.onPause()
        stopRecord()
    }

    fun stopRecord(){
        _isRecording = false
        _button?.text = "start"
        _record?.cancel(true)
    }

    fun doRecord(){
        _isRecording = true
        _button?.text = "stop"

        // AsyncTaskは使い捨て１回こっきりなので毎回作ります
        _record = Record()
        _record?.execute()
    }

    inner class Record : AsyncTask<Void, DoubleArray, Void>() {
        override fun doInBackground(vararg params: Void): Void? {
            // サンプリングレート。1秒あたりのサンプル数
            // （8000, 11025, 22050, 44100, エミュでは8kbじゃないとだめ？）
            val sampleRate = 44100

            // 最低限のバッファサイズ
            val minBufferSize = AudioRecord.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT) * 2

            // バッファサイズが取得できない。サンプリングレート等の設定を端末がサポートしていない可能性がある。
            if(minBufferSize < 0){
                return null
            }

            val audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minBufferSize)

            val sec = 1
            val buffer: ShortArray = ShortArray(sampleRate * (16 / 8) * 1 * sec)

            audioRecord.startRecording()

            try {
                while (_isRecording) {
                    val readSize = audioRecord.read(buffer, 0, minBufferSize)

                    if (readSize < 0) {
                        break
                    }
                    if (readSize == 0) {
                        continue
                    }
                    var maxSound:Int = 0
                    for(i in 0..readSize)
                    {
                        maxSound= max(maxSound,buffer[i].toInt())

                    }
                    Log.v("VolMax",maxSound.toString())
                    //_visualizer?.update(buffer, readSize)
                }
            } finally {
                audioRecord.stop()
                audioRecord.release()
            }

            return null
        }
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
