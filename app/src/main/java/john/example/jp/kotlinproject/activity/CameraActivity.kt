package john.example.jp.kotlinproject

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ImageReader
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.constraint.ConstraintSet.INVISIBLE
import android.support.constraint.ConstraintSet.VISIBLE
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import john.example.jp.kotlinproject.R
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Date
import java.text.SimpleDateFormat


class CameraActivity : AppCompatActivity() {

    private var mCamera: CameraStateMachine? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        mCamera = CameraStateMachine()
    }


    override fun onResume() {
        super.onResume()
        (mCamera as CameraStateMachine).open(this, textureView)
    }

    override fun onPause() {
        (mCamera as CameraStateMachine).close()
        super.onPause()
    }

    @SuppressLint("WrongConstant")
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && imageView.getVisibility() === VISIBLE) {
            textureView.setVisibility(VISIBLE)
            imageView.setVisibility(INVISIBLE)
            return false
        }
        return super.onKeyDown(keyCode, event)
    }

    fun onClickShutter(view: View) {
        (mCamera as CameraStateMachine).takePicture(object : ImageReader.OnImageAvailableListener {
            @SuppressLint("WrongConstant")
            override fun onImageAvailable(reader: ImageReader) {
                // 撮れた画像をImageViewに貼り付けて表示。
                val image = reader.acquireLatestImage()
                val buffer = image.getPlanes()[0].getBuffer()
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                image.close()

                val matrix = Matrix()
                matrix.postRotate(90f)
                val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                imageView.setImageBitmap(rotatedBitmap)
                imageView.setVisibility(VISIBLE)
                textureView.setVisibility(INVISIBLE)

                saveBitmap(rotatedBitmap)
            }
        })
    }

    @Throws(IOException::class)
    fun saveBitmap(saveImage: Bitmap) {

        val SAVE_DIR = "/MyPhoto/"
        val file = File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR)
        try {
            if (!file.exists()) {
                file.mkdir()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            throw e
        }

        val mDate = Date(System.currentTimeMillis())
        val fileNameDate = SimpleDateFormat("yyyyMMdd_HHmmss")
        val fileName = fileNameDate.format(mDate) + ".jpg"
        val AttachName = file.getAbsolutePath() + "/" + fileName

        try {
            val out = FileOutputStream(AttachName)
            saveImage.compress(CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
            throw e
        }

        // save index
        val values = ContentValues()
        val contentResolver = contentResolver
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.TITLE, fileName)
        values.put("_data", AttachName)
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
}
