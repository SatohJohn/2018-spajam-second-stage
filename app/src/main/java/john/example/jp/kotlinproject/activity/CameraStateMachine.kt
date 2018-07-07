package john.example.jp.kotlinproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.hardware.camera2.CameraAccessException
import android.media.ImageReader
import android.os.Handler
import android.util.Log
import android.view.Surface
import android.view.TextureView
import java.util.*

class CameraStateMachine {

    private val TAG = CameraStateMachine::class.java!!.getSimpleName()
    private var mCameraManager: CameraManager? = null

    private var mCameraDevice: CameraDevice? = null
    private var mCaptureSession: CameraCaptureSession? = null
    private var mImageReader: ImageReader? = null
    private var mPreviewRequestBuilder: CaptureRequest.Builder? = null

    private var mTextureView: AutoFitTextureView? = null
    private var mHandler: Handler? = null // default current thread.
    private var mState: State? = null
    private var mTakePictureListener: ImageReader.OnImageAvailableListener? = null

    fun open(activity: Activity, textureView: AutoFitTextureView) {
        if (mState != null) throw IllegalStateException("Alrady started state=" + mState!!)
        mTextureView = textureView
        mCameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        nextState(mInitSurfaceState)
    }

    fun takePicture(listener: ImageReader.OnImageAvailableListener): Boolean {
        if (mState !== mPreviewState) return false
        mTakePictureListener = listener
        nextState(mAutoFocusState)
        return true
    }

    fun close() {
        nextState(mAbortState)
    }

    // ----------------------------------------------------------------------------------------
    // The following private
    private fun shutdown() {
        if (null != mCaptureSession) {
            mCaptureSession!!.close()
            mCaptureSession = null
        }
        if (null != mCameraDevice) {
            mCameraDevice!!.close()
            mCameraDevice = null
        }
        if (null != mImageReader) {
            mImageReader!!.close()
            mImageReader = null
        }
    }

    private fun nextState(nextState: State?) {
        Log.d(TAG, "state: $mState->$nextState")
        try {
            if (mState != null) mState!!.finish()
            mState = nextState
            if (mState != null) mState!!.enter()
        } catch (e: CameraAccessException) {
            Log.e(TAG, "next($nextState)", e)
            shutdown()
        }

    }

    private abstract inner class State(private val mName: String) {
        //@formatter:off
        override fun toString(): String {
            return mName
        }

        @Throws(CameraAccessException::class)
        open fun enter() {
        }

        open fun onSurfaceTextureAvailable(width: Int, height: Int) {}
        open fun onCameraOpened(cameraDevice: CameraDevice) {}
        open fun onSessionConfigured(cameraCaptureSession: CameraCaptureSession) {}
        @Throws(CameraAccessException::class)
        open fun onCaptureResult(result: CaptureResult, isCompleted: Boolean) {
        }

        @Throws(CameraAccessException::class)
        open fun finish() {
        }
        //@formatter:on
    }

    // ===================================================================================
    // State Definition
    private var mInitSurfaceState = object : State("InitSurface") {

        private var mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
                if (mState != null) mState!!.onSurfaceTextureAvailable(width, height)
            }

            override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
                // TODO: ratation changed.
            }

            override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
                return true
            }

            override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
        }

        @Throws(CameraAccessException::class)
        override fun enter() {
            if ((mTextureView as AutoFitTextureView).isAvailable()) {
                nextState(mOpenCameraState)
            } else {
                (mTextureView as AutoFitTextureView).setSurfaceTextureListener(mSurfaceTextureListener)
            }
        }

        override fun onSurfaceTextureAvailable(width: Int, height: Int) {
            nextState(mOpenCameraState)
        }
    }
    // -----------------------------------------------------------------------------------
    private var mOpenCameraState = object : State("OpenCamera") {

        private var mStateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(cameraDevice: CameraDevice) {
                if (mState != null) mState!!.onCameraOpened(cameraDevice)
            }

            override fun onDisconnected(cameraDevice: CameraDevice) {
                nextState(mAbortState)
            }

            override fun onError(cameraDevice: CameraDevice, error: Int) {
                Log.e(TAG, "CameraDevice:onError:$error")
                nextState(mAbortState)
            }
        }

        @SuppressLint("MissingPermission")
        @Throws(CameraAccessException::class)
        override fun enter() {
            for (cameraId in (mCameraManager as CameraManager).getCameraIdList()) {                   // ->(10)
                var characteristics = (mCameraManager as CameraManager).getCameraCharacteristics(cameraId)
                if (characteristics.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {                    // ->(11)
                    var backCameraId = cameraId
                    var map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)

                    mImageReader = CameraUtil.getMaxSizeImageReader(map, ImageFormat.JPEG)
                    var previewSize = CameraUtil.getBestPreviewSize(map, mImageReader as ImageReader)
                    (mTextureView as AutoFitTextureView).setPreviewSize(previewSize.height, previewSize.width)

                    (mCameraManager as CameraManager).openCamera(cameraId, mStateCallback, mHandler)
                }
            }
        }

        override fun onCameraOpened(cameraDevice: CameraDevice) {
            mCameraDevice = cameraDevice
            nextState(mCreateSessionState)
        }
    }
    // -----------------------------------------------------------------------------------
    private var mCreateSessionState = object : State("CreateSession") {

        private var mSessionCallback = object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                if (mState != null) mState!!.onSessionConfigured(cameraCaptureSession)
            }

            override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                nextState(mAbortState)
            }
        }

        @Throws(CameraAccessException::class)
        override fun enter() {
            var texture = (mTextureView as AutoFitTextureView).getSurfaceTexture()
//            texture.setDefaultBufferSize(mTextureView!!.previewWidth, mTextureView!!.previewHeight)
            texture.setDefaultBufferSize(CameraUtil.resolutionX, CameraUtil.resolutionY)
            var surface = Surface(texture)

            mPreviewRequestBuilder = (mCameraDevice as CameraDevice).createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            (mPreviewRequestBuilder as CaptureRequest.Builder).addTarget(surface)
            var outputs = Arrays.asList(surface, mImageReader!!.surface)
            (mCameraDevice as CameraDevice).createCaptureSession(outputs, mSessionCallback, mHandler)
        }

        override fun onSessionConfigured(cameraCaptureSession: CameraCaptureSession) {
            mCaptureSession = cameraCaptureSession
            nextState(mPreviewState)
        }
    }
    // -----------------------------------------------------------------------------------
    private var mPreviewState = object : State("Preview") {
        @Throws(CameraAccessException::class)
        override fun enter() {
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            mCaptureSession!!.setRepeatingRequest(mPreviewRequestBuilder!!.build(), mCaptureCallback, mHandler)
        }
    }
    private var mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureProgressed(session: CameraCaptureSession, request: CaptureRequest, partialResult: CaptureResult) {
            onCaptureResult(partialResult, false)
        }

        override fun onCaptureCompleted(session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult) {
            onCaptureResult(result, true)
        }

        private fun onCaptureResult(result: CaptureResult, isCompleted: Boolean) {
            try {
                if (mState != null) (mState as State).onCaptureResult(result, isCompleted)
            } catch (e: CameraAccessException) {
                Log.e(TAG, "handle():", e)
                nextState(mAbortState)
            }

        }
    }
    // -----------------------------------------------------------------------------------
    private var mAutoFocusState = object : State("AutoFocus") {
        @Throws(CameraAccessException::class)
        override fun enter() {
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START)
            mCaptureSession!!.setRepeatingRequest(mPreviewRequestBuilder!!.build(), mCaptureCallback, mHandler)
        }

        @Throws(CameraAccessException::class)
        override fun onCaptureResult(result: CaptureResult, isCompleted: Boolean) {
            var afState = result.get(CaptureResult.CONTROL_AF_STATE)
            var isAfReady = (afState == null
                    || afState == CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED
                    || afState == CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED)
            if (isAfReady) {
                nextState(mAutoExposureState)
            }
        }
    }
    // -----------------------------------------------------------------------------------
    private var mAutoExposureState = object : State("AutoExposure") {
        @Throws(CameraAccessException::class)
        override fun enter() {
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START)
            mCaptureSession!!.setRepeatingRequest(mPreviewRequestBuilder!!.build(), mCaptureCallback, mHandler)
        }

        @Throws(CameraAccessException::class)
        override fun onCaptureResult(result: CaptureResult, isCompleted: Boolean) {
            var aeState = result.get(CaptureResult.CONTROL_AE_STATE)
            var isAeReady = (aeState == null
                    || aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED
                    || aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED)
            if (isAeReady) {
                nextState(mTakePictureState)
            }
        }
    }
    // -----------------------------------------------------------------------------------
    private var mTakePictureState = object : State("TakePicture") {
        @Throws(CameraAccessException::class)
        override fun enter() {
            var captureBuilder = mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
            captureBuilder.addTarget(mImageReader!!.getSurface())
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, 90) // portraito
            mImageReader!!.setOnImageAvailableListener(mTakePictureListener, mHandler)

            mCaptureSession!!.stopRepeating()
            mCaptureSession!!.capture(captureBuilder.build(), mCaptureCallback, mHandler)
        }

        @Throws(CameraAccessException::class)
        override fun onCaptureResult(result: CaptureResult, isCompleted: Boolean) {
            if (isCompleted) {
                nextState(mPreviewState)
            }
        }

        @Throws(CameraAccessException::class)
        override fun finish() {
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
            mPreviewRequestBuilder!!.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            mCaptureSession!!.capture(mPreviewRequestBuilder!!.build(), mCaptureCallback, mHandler)
            mTakePictureListener = null
        }
    }
    // -----------------------------------------------------------------------------------
    private var mAbortState = object : State("Abort") {
        @Throws(CameraAccessException::class)
        override fun enter() {
            shutdown()
            nextState(null)
        }
    }
}

