package com.example.thesis.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log // Added for logging
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit

class VideoRepository(private val context: Context, private val backendUrl: String) {

    private val TAG = "VIDEO_REPO"

    // Increased timeouts for video processing
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES) // Increased to 5 mins
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .build()

    fun uploadVideo(videoUri: Uri, onResult: (Boolean, File?) -> Unit) {
        Log.d(TAG, "Starting upload process for URI: $videoUri")

        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(videoUri)

        if (inputStream == null) {
            Log.e(TAG, "Failed to open InputStream from Uri")
            onResult(false, null)
            return
        }

        try {
            // Prepare temp file to send
            val tempFile = File.createTempFile("upload", ".mp4", context.cacheDir)
            tempFile.outputStream().use { inputStream.copyTo(it) }

            Log.d(TAG, "Temp file created: ${tempFile.absolutePath} (${tempFile.length() / 1024} KB)")

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "video",
                    tempFile.name,
                    tempFile.asRequestBody("video/mp4".toMediaTypeOrNull())
                )
                .build()

            val request = Request.Builder()
                .url(backendUrl)
                .post(requestBody)
                .build()

            Log.d(TAG, "Sending request to: $backendUrl")

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Network Error: ${e.message}")
                    onResult(false, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.d(TAG, "Response Code: ${response.code}")

                    if (response.isSuccessful) {
                        try {
                            val processedFile = File(context.cacheDir, "processed_${System.currentTimeMillis()}.mp4")

                            response.body?.byteStream()?.use { input ->
                                processedFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }

                            Log.d(TAG, "Processed video saved to: ${processedFile.absolutePath} (${processedFile.length() / 1024} KB)")
                            onResult(true, processedFile)
                        } catch (e: Exception) {
                            Log.e(TAG, "Error saving processed video: ${e.message}")
                            onResult(false, null)
                        }
                    } else {
                        Log.e(TAG, "Server Error Response: ${response.body?.string()}")
                        onResult(false, null)
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Preparation Error: ${e.message}")
            onResult(false, null)
        }
    }
}