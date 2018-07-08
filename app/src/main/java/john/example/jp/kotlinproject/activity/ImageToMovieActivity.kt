package john.example.jp.kotlinproject.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import john.example.jp.kotlinproject.CameraUtil
import org.jcodec.api.SequenceEncoder;
import java.io.File
import john.example.jp.kotlinproject.utils.CopyToPicture
import android.provider.MediaStore
import android.graphics.Bitmap
import android.content.ContentResolver
import android.net.Uri


class ImageToMovieActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val enc = SequenceEncoder.createSequenceEncoder(File(CameraUtil.getDirectoryInfo().absolutePath + "/" + CameraUtil.getFileName(".mp4")), 30)

        // 画像を入れる
        val fileList = CameraUtil.getDirectoryInfo()
                .listFiles()
                .filter { file -> file.extension == "jpg" || file.extension == "png" }
                .map { file ->
                    run {
                        val cr = contentResolver
                        val image = MediaStore.Images.Media.getBitmap(cr, Uri.fromFile(file))
//                        val decodeFile = BitmapFactory.decodeFile(file.absolutePath)
                        CopyToPicture.fromBitmap(image)
                    }
                }

        fileList.forEach{ picture ->
            for (i in 1..60) enc.encodeNativeFrame(picture)
        }

        enc.finish()
    }
//
//    fun fromBitmap(src: Bitmap): Picture {
//        val dst = Picture.create(src.width, src.height, RGB)
//        fromBitmap(src, dst)
//        return dst
//    }
//
//    fun fromBitmap(src: Bitmap, dst: Picture) {
//        val dstData = dst.getPlaneData(0)
//        val packed = IntArray(src.width * src.height)
//
//        src.getPixels(packed, 0, src.width, 0, 0, src.width, src.height)
//
//        var i = 0
//        var srcOff = 0
//        var dstOff = 0
//        while (i < src.height) {
//            var j = 0
//            while (j < src.width) {
//                val rgb: Int = packed[srcOff]
//                dstData[dstOff] = (rgb >> 16 & 0xff)
//                dstData[dstOff + 1] = (rgb shr 8 and 0xff).toByte()
//                dstData[dstOff + 2] = (rgb and 0xff).toByte()
//                j++
//                srcOff++
//                dstOff += 3
//            }
//            i++
//        }
//    }

}