package john.example.jp.kotlinproject.activity

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.coremedia.iso.boxes.Container
import john.example.jp.kotlinproject.R
import java.io.*
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Track
import java.util.*
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import com.googlecode.mp4parser.util.Matrix.ROTATE_180
import com.coremedia.iso.boxes.MovieHeaderBox
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.util.Matrix
import com.googlecode.mp4parser.util.Path
import john.example.jp.kotlinproject.utils.MovieFileTrimer


class TestActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val SAVE_DIR = "/MyPhoto/"
        val saveFolder = File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR)
        val movieFile = saveFolder.listFiles { dir, fileName: String -> fileName.endsWith(".mp4")
        }.first()
        // movieFileがなかったら死亡
        Log.i(this::class.java.simpleName, movieFile.name)
//        val destFile = File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR + "test.mp4")
//        if (!destFile.exists()) {
//            destFile.createNewFile()
//        }

        // ミリ秒です
        val startMs = 1000
        val endMs = 3500

        MovieFileTrimer.test(movieFile, saveFolder, startMs, endMs)
    }
}