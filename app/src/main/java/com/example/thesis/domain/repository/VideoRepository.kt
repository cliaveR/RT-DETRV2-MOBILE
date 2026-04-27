package com.example.thesis.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.thesis.model.data.mapTracking.VideoCaptureCoordinates
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class VideoRepository(private val context: Context, private val backendUrl: String) {

    private val tag = "VIDEO_REPO"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .build()

    fun uploadVideo(videoUri: Uri, onResult: (Boolean, File?) -> Unit) {
        uploadVideo(videoUri, coordinates = null, onResult = onResult)
    }

    fun uploadVideo(
        videoUri: Uri,
        coordinates: VideoCaptureCoordinates?,
        onResult: (Boolean, File?) -> Unit
    ) {
        Log.d(tag, "Starting upload process for URI: $videoUri")
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(videoUri)

        if (inputStream == null) {
            Log.e(tag, "Failed to open InputStream from Uri")
            onResult(false, null)
            return
        }

        try {
            val tempFile = File.createTempFile("upload", ".mp4", context.cacheDir)
            tempFile.outputStream().use { inputStream.copyTo(it) }

            val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "video",
                    tempFile.name,
                    tempFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                )

            coordinates?.start?.let {
                multipartBuilder
                    .addFormDataPart("start_latitude", it.latitude.toString())
                    .addFormDataPart("start_longitude", it.longitude.toString())
            }

            coordinates?.end?.let {
                multipartBuilder
                    .addFormDataPart("end_latitude", it.latitude.toString())
                    .addFormDataPart("end_longitude", it.longitude.toString())
            }

            Log.d(tag, "Bundled video GPS metadata=$coordinates")

            val request = Request.Builder()
                .url(backendUrl)
                .post(multipartBuilder.build())
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(tag, "Network Error: ${e.message}")
                    onResult(false, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(tag, "Response Code: ${response.code}")
                    if (response.isSuccessful) {
                        try {
                            val processedFile = File(
                                context.cacheDir,
                                "processed_${System.currentTimeMillis()}.mp4"
                            )
                            response.body?.byteStream()?.use { input ->
                                processedFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            onResult(true, processedFile)
                        } catch (e: Exception) {
                            Log.e(tag, "Error saving processed video: ${e.message}")
                            onResult(false, null)
                        }
                    } else {
                        Log.e(tag, "Server Error Response: ${response.body?.string()}")
                        onResult(false, null)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "Preparation Error: ${e.message}")
            onResult(false, null)
        }
    }
}