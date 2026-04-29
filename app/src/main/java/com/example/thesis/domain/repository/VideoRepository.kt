package com.example.thesis.domain.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.thesis.model.data.DamageVideoItem
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
import java.io.OutputStream
import java.util.concurrent.TimeUnit

class VideoRepository(private val context: Context, private val backendUrl: String) {

    private val tag = "VIDEO_REPO"

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .build()

    fun uploadVideo(videoUri: Uri, onResult: (Boolean, Uri?) -> Unit) {
        uploadVideo(videoUri, coordinates = null, onResult = onResult)
    }

    fun uploadVideo(
        videoUri: Uri,
        coordinates: VideoCaptureCoordinates?,
        onResult: (Boolean, Uri?) -> Unit
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
                            val videoBytes = response.body?.bytes()
                            if (videoBytes != null) {
                                val savedUri = saveVideoToGallery(videoBytes)
                                onResult(true, savedUri)
                            } else {
                                onResult(false, null)
                            }
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

    private fun saveVideoToGallery(bytes: ByteArray): Uri? {
        return try {
            val filename = "Thesis_Processed_${System.currentTimeMillis()}.mp4"
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, filename)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/ThesisApp")
            }

            val uri = context.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return null

            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            outputStream?.use {
                it.write(bytes)
                it.flush()
            }
            Log.d(tag, "Video saved to gallery: $uri")
            uri
        } catch (e: Exception) {
            Log.e(tag, "Save to gallery failed: ${e.message}")
            null
        }
    }

    fun getDamageVideosFromGallery(): List<DamageVideoItem> {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED
        )
        val selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ? AND (${MediaStore.Video.Media.DISPLAY_NAME} LIKE ? OR ${MediaStore.Video.Media.DISPLAY_NAME} LIKE ?)"
        val selectionArgs = arrayOf("%Movies/ThesisApp%", "Thesis_Processed_%", "processed_%")
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val results = mutableListOf<DamageVideoItem>()

        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dateIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val displayName = it.getString(nameIndex) ?: "Processed Video"
                val dateAddedSeconds = it.getLong(dateIndex)

                results += DamageVideoItem(
                    uri = Uri.withAppendedPath(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    ),
                    displayName = displayName,
                    dateAddedSeconds = dateAddedSeconds
                )
            }
        }
        return results
    }
}
