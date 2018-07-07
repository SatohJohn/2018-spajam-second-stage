package john.example.jp.kotlinproject

import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.params.StreamConfigurationMap
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.util.Size

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
    }

}