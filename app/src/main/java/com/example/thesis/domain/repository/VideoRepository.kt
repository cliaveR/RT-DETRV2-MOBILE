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
import org.json.JSONObject
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
        val startTime = System.currentTimeMillis()
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(videoUri)

        if (inputStream == null) {
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

            val request = Request.Builder()
                .url(backendUrl)
                .post(multipartBuilder.build())
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onResult(false, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val videoBytes = response.body?.bytes()
                            if (videoBytes != null) {
                                val processingTime = (System.currentTimeMillis() - startTime).toInt()
                                val savedUri = saveVideoToGallery(videoBytes, processingTime, coordinates)
                                onResult(true, savedUri)
                            } else {
                                onResult(false, null)
                            }
                        } catch (e: Exception) {
                            onResult(false, null)
                        }
                    } else {
                        onResult(false, null)
                    }
                }
            })
        } catch (e: Exception) {
            onResult(false, null)
        }
    }

    private fun saveVideoToGallery(bytes: ByteArray, processingTimeMs: Int? = null, coords: VideoCaptureCoordinates? = null): Uri? {
        return try {
            val timestamp = System.currentTimeMillis()
            val filename = "Thesis_Processed_${timestamp}.mp4"
            val contentValues = ContentValues().apply {
                put(MediaStore.Video.Media.DISPLAY_NAME, filename)
                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/ThesisApp")
            }

            val uri = context.contentResolver.insert(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return null

            context.contentResolver.openOutputStream(uri)?.use {
                it.write(bytes)
                it.flush()
            }

            saveVideoMetadata(timestamp, processingTimeMs, coords)
            
            uri
        } catch (e: Exception) {
            null
        }
    }

    private fun saveVideoMetadata(timestamp: Long, processingTimeMs: Int?, coords: VideoCaptureCoordinates?) {
        try {
            val metaDir = File(context.filesDir, "video_metadata")
            if (!metaDir.exists()) metaDir.mkdirs()
            val metaFile = File(metaDir, "meta_${timestamp}.json")
            val meta = JSONObject().apply {
                processingTimeMs?.let { put("processingTimeMs", it) }
                coords?.start?.let {
                    put("start_latitude", it.latitude)
                    put("start_longitude", it.longitude)
                }
                coords?.end?.let {
                    put("end_latitude", it.latitude)
                    put("end_longitude", it.longitude)
                }
            }
            metaFile.writeText(meta.toString())
        } catch (e: Exception) {
            Log.e(tag, "Failed to save video metadata: ${e.message}")
        }
    }

    private fun loadVideoMetadata(displayName: String): Map<String, Any?> {
        return try {
            val timestamp = displayName
                .removePrefix("Thesis_Processed_")
                .removeSuffix(".mp4")
                .toLongOrNull() ?: return emptyMap()

            val metaFile = File(File(context.filesDir, "video_metadata"), "meta_${timestamp}.json")
            if (!metaFile.exists()) return emptyMap()
            
            val json = JSONObject(metaFile.readText())
            val map = mutableMapOf<String, Any?>()
            if (json.has("processingTimeMs")) map["processingTimeMs"] = json.getInt("processingTimeMs")
            if (json.has("start_latitude")) map["start_latitude"] = json.getDouble("start_latitude")
            if (json.has("start_longitude")) map["start_longitude"] = json.getDouble("start_longitude")
            if (json.has("end_latitude")) map["end_latitude"] = json.getDouble("end_latitude")
            if (json.has("end_longitude")) map["end_longitude"] = json.getDouble("end_longitude")
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun getDamageVideosFromGallery(): List<DamageVideoItem> {
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DATE_ADDED
        )
        val selection = "${MediaStore.Video.Media.RELATIVE_PATH} LIKE ? AND ${MediaStore.Video.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%Movies/ThesisApp%", "Thesis_Processed_%")
        val sortOrder = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val results = mutableListOf<DamageVideoItem>()

        context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val displayName = cursor.getString(nameIndex) ?: continue
                val meta = loadVideoMetadata(displayName)

                results += DamageVideoItem(
                    uri = Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, cursor.getLong(idIndex).toString()),
                    displayName = displayName,
                    dateAddedSeconds = cursor.getLong(dateIndex),
                    processingTimeMs = meta["processingTimeMs"] as? Int,
                    startLatitude = meta["start_latitude"] as? Double,
                    startLongitude = meta["start_longitude"] as? Double,
                    endLatitude = meta["end_latitude"] as? Double,
                    endLongitude = meta["end_longitude"] as? Double
                )
            }
        }
        return results
    }

    fun getVideoDetails(uri: Uri): DamageVideoItem? {
        val projection = arrayOf(MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATE_ADDED)
        return try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                    val meta = loadVideoMetadata(displayName)
                    DamageVideoItem(
                        uri = uri,
                        displayName = displayName,
                        dateAddedSeconds = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)),
                        processingTimeMs = meta["processingTimeMs"] as? Int,
                        startLatitude = meta["start_latitude"] as? Double,
                        startLongitude = meta["start_longitude"] as? Double,
                        endLatitude = meta["end_latitude"] as? Double,
                        endLongitude = meta["end_longitude"] as? Double
                    )
                } else null
            }
        } catch (e: Exception) { null }
    }
}
