package john.example.jp.kotlinproject.utils

import android.media.MediaPlayer
import android.util.Log
import com.coremedia.iso.boxes.Container
import com.coremedia.iso.boxes.MovieHeaderBox
import com.googlecode.mp4parser.FileDataSourceImpl
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack
import com.googlecode.mp4parser.util.Matrix
import com.googlecode.mp4parser.util.Path
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.math.log
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import kotlin.collections.ArrayList
import android.os.Environment.getExternalStorageDirectory




object MovieFileBinder {
    /**
     * workingDirectoryは存在していないといけない
     * destFileは存在していなくても良い
     */
    fun test(workingDirectory: File, destFile: File) {

        try {
            val movieList = workingDirectory.listFiles()
                    .map { it.absolutePath }
                    .map { path -> MovieCreator.build(path) }

            // 1つのファイルに結合
            val videoTracks = ArrayList<Track>()
            val audioTracks = ArrayList<Track>()
            for (movie: Movie in movieList) {
                for (t in movie.getTracks()) {
                    if (t.getHandler() == "soun") {
                        audioTracks.add(t)
                    }
                    if (t.getHandler() == "vide") {
                        videoTracks.add(t)
                    }
                }
            }
            val result = Movie()
            if (audioTracks.size > 0) {
                result.addTrack(AppendTrack(*audioTracks.toTypedArray()))
            }
            if (videoTracks.size > 0) {
                result.addTrack(AppendTrack(*videoTracks.toTypedArray()))
            }


            // 出力
            val out = DefaultMp4Builder().build(result)
            if (!destFile.exists()) {
                Log.i(this::class.java.simpleName, destFile.absolutePath)
                destFile.createNewFile()
            }
            val fos = FileOutputStream(destFile)
            out.writeContainer(fos.getChannel())
            fos.close()
        } catch (e: Exception) {
            Log.w(this::class.java.simpleName, e)
        }
    }
}