package john.example.jp.kotlinproject

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.params.StreamConfigurationMap
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Date
import java.text.SimpleDateFormat

class CameraUtil {

    companion object {
        open val resolutionX : Int = 1920
        open val resolutionY : Int = 1440

        @Throws(CameraAccessException::class)
        fun getCameraId(cameraManager: CameraManager, facing: Int): String? {
            for (cameraId in cameraManager.cameraIdList) {
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == facing) {
                    return cameraId
                }
            }
            return null
        }

        @Throws(CameraAccessException::class)
        fun getMaxSizeImageReader(map: StreamConfigurationMap, imageFormat: Int): ImageReader {
            val sizes = map.getOutputSizes(imageFormat)
            var maxSize = sizes[0]
            for (size in sizes) {
                if (size.width > maxSize.width) {
                    maxSize = size
                }
            }
            /*return ImageReader.newInstance(
                    //maxSize.getWidth(), maxSize.getHeight(), // for landscape.
                    maxSize.height, maxSize.width, // for portrait.
                    imageFormat, *//*maxImages*//*2)*/
            return ImageReader.newInstance(resolutionX, resolutionY, imageFormat, 1)
        }

        @Throws(CameraAccessException::class)
        fun getBestPreviewSize(map: StreamConfigurationMap, imageSize: ImageReader): Size {
            //float imageAspect = (float) imageSize.getWidth() / imageSize.getHeight(); // for landscape.
            val imageAspect = imageSize.height.toFloat() / imageSize.width.toFloat() // for portrait
            var minDiff = 10000f
            val previewSizes = map.getOutputSizes(SurfaceTexture::class.java)
            var previewSize = previewSizes[0]
            for (size in previewSizes) {
                val previewAspect = size.width.toFloat() / size.height.toFloat()
                val diff = Math.abs(imageAspect - previewAspect)
                if (diff < minDiff) {
                    previewSize = size
                    minDiff = diff
                }
                if (diff == 0.0f) break
            }
//            return previewSize
            return Size(resolutionX, resolutionY)
        }

        @Throws(IOException::class)
        fun saveBitmap(saveImage: Bitmap) {

            var fileName = getFileName(".jpg")
            val AttachName = getDirectoryInfo().getAbsolutePath() + "/" + fileName

            try {
                val out = FileOutputStream(AttachName)
                saveImage.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
                throw e
            }

            // save index
//            val values = ContentValues()
//            val contentResolver = contentResolver
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//            values.put(MediaStore.Images.Media.TITLE, fileName)
//            values.put("_data", AttachName)
//            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }

        fun getDirectoryInfo() : File{

            val SAVE_DIR = "/idol/"
            return File(Environment.getExternalStorageDirectory().getPath() + SAVE_DIR)
        }

        fun getFileName(extension : String) : String{

            val file = getDirectoryInfo()
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
            return fileNameDate.format(mDate) + extension

        }
    }

}