package john.example.jp.kotlinproject.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import john.example.jp.kotlinproject.R
import kotlinx.android.synthetic.main.activity_debug.*

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        debugAudioSeekBar.setOnSeekBarChangeListener(DebugSeekBar())
        debugAudioSeekBar.progress = 1000
    }

    inner class DebugSeekBar: SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            Log.i(this::class.java.simpleName, "${progress} : ${fromUser}")
            debugAudioText.text = "音の大きさ: ${progress}"
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
        }

    }
}