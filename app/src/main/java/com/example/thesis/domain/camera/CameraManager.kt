package com.example.thesis.domain.camera

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat

class CameraManager(private val context: Context) {
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private var imageCapture: ImageCapture? = null
    fun buildVideoCapture(): VideoCapture<Recorder>{
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
            .build()
        return VideoCapture.withOutput(recorder).also { this.videoCapture=it }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun startRecording(onVideoSaved: (Uri?)-> Unit){
        val name = "Thesis_Video_${System.currentTimeMillis()}.mp4"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Movies/ThesisApp")
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(contentValues).build()

        recording = videoCapture?.output
            ?.prepareRecording(context,mediaStoreOutput)
            ?.withAudioEnabled()
            ?.start(ContextCompat.getMainExecutor(context)){
                event -> if(event is VideoRecordEvent.Finalize){
                    onVideoSaved(if(!event.hasError()) event.outputResults.outputUri else null)
            }
            }
    }

    fun buildImageCapture(): ImageCapture{
        return ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
            .also { this.imageCapture = it }
    }

    fun takePhoto(onPhotoSaved: (Uri?) -> Unit) {
        val name = "Thesis_Photo_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/ThesisApp")
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onPhotoSaved(outputFileResults.savedUri)
                }

                override fun onError(exception: ImageCaptureException) {
                    onPhotoSaved(null)
                }
            }
        )
    }
    fun stopRecording() = recording?.stop()
}