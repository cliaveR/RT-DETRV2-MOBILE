package com.example.thesis.domain.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import com.example.thesis.model.data.DamageImageItem
import com.example.thesis.model.data.mapTracking.GeoCoordinate
import com.example.thesis.model.data.mapTracking.PhotoUploadResult
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.OutputStream
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class PhotoRepository(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
        
    private val tag = "PICTURE"

    suspend fun uploadAndSaveVisualized(
        uri: Uri,
        captureCoordinate: GeoCoordinate? = null
    ): PhotoUploadResult? = suspendCancellableCoroutine { continuation ->
        try {
            Log.d(tag, "Opening input stream for URI: $uri")
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes() ?: throw Exception("Could not read image")
            inputStream.close()

            val multipartBuilder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "image",
                    "thesis_photo.jpg",
                    bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                )

            captureCoordinate?.let {
                multipartBuilder
                    .addFormDataPart("latitude", it.latitude.toString())
                    .addFormDataPart("longitude", it.longitude.toString())
            }

            val request = Request.Builder()
                .url("http://192.168.254.200:8080/api/upload/visualize")
                .post(multipartBuilder.build())
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.e(tag, "Upload failed network error: ${e.message}")
                    if (continuation.isActive) continuation.resume(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        val responseText = it.body?.string()
                        if (!it.isSuccessful) {
                            if (continuation.isActive) continuation.resume(null)
                            return
                        }

                        try {
                            val json = JSONObject(responseText ?: "{}")
                            val frameId = json.optString("frameId")
                            val inferenceData = json.opt("inferenceData")?.toString()
                            val processingTimeMs = json.optInt("processingTimeMs", -1)
                            val imageBase64 = json.optString("imageBase64")
                            val latitude = json.optDouble("latitude", Double.NaN).takeIf { !it.isNaN() }
                            val longitude = json.optDouble("longitude", Double.NaN).takeIf { !it.isNaN() }

                            Log.d(tag, "Received inferenceData from server: $inferenceData")

                            val savedUri = if (!imageBase64.isNullOrBlank()) {
                                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                                saveImageToGallery(imageBytes, inferenceData)
                            } else null

                            if (continuation.isActive) {
                                continuation.resume(
                                    PhotoUploadResult(
                                        frameId = frameId,
                                        processingTimeMs = if (processingTimeMs >= 0) processingTimeMs else null,
                                        inferenceData = inferenceData,
                                        savedImageUri = savedUri,
                                        latitude = latitude,
                                        longitude = longitude
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resume(null)
        }
    }

    private fun saveImageToGallery(bytes: ByteArray, inferenceData: String? = null): Uri? {
        return try {
            val timestamp = System.currentTimeMillis()
            val filename = "Thesis_Processed_${timestamp}.jpg"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ThesisApp")
                // DESCRIPTION is unreliable on Android 10+ scoped storage
                // We'll store inference data in a sidecar .json file instead
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return null

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(bytes)
                outputStream.flush()
            }

            // Save inference data as a sidecar JSON file in app's private storage
            if (!inferenceData.isNullOrBlank()) {
                saveInferenceMetadata(timestamp, inferenceData)
            }

            Log.d(tag, "Saved image to gallery, metadata saved for timestamp: $timestamp")
            uri
        } catch (e: Exception) {
            Log.e(tag, "Failed to save image: ${e.message}")
            null
        }
    }

    private fun saveInferenceMetadata(timestamp: Long, inferenceData: String) {
        try {
            val metaDir = java.io.File(context.filesDir, "inference_metadata")
            if (!metaDir.exists()) metaDir.mkdirs()
            val metaFile = java.io.File(metaDir, "meta_${timestamp}.json")
            metaFile.writeText(inferenceData)
            Log.d(tag, "Inference metadata saved: ${metaFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(tag, "Failed to save inference metadata: ${e.message}")
        }
    }

    // Load inference metadata for a given image filename
    private fun loadInferenceMetadata(displayName: String): String? {
        return try {
            // Extract timestamp from filename: "Thesis_Processed_1234567890.jpg"
            val timestamp = displayName
                .removePrefix("Thesis_Processed_")
                .removeSuffix(".jpg")
                .toLongOrNull() ?: return null

            val metaDir = java.io.File(context.filesDir, "inference_metadata")
            val metaFile = java.io.File(metaDir, "meta_${timestamp}.json")
            if (metaFile.exists()) metaFile.readText() else null
        } catch (e: Exception) {
            Log.e(tag, "Failed to load inference metadata: ${e.message}")
            null
        }
    }
    fun getDamageImagesFromGallery(): List<DamageImageItem> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
            // Removed DESCRIPTION - unreliable on Android 10+
        )
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Pictures/ThesisApp%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val results = mutableListOf<DamageImageItem>()

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val displayName = cursor.getString(nameIndex) ?: continue
                // Load from sidecar file instead of DESCRIPTION column
                val inferenceData = loadInferenceMetadata(displayName)

                results += DamageImageItem(
                    uri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(idIndex).toString()
                    ),
                    displayName = displayName,
                    dateAddedSeconds = cursor.getLong(dateIndex),
                    inferenceData = inferenceData
                )
            }
        }
        return results
    }

    fun getImageDetails(uri: Uri): DamageImageItem? {
        val projection = arrayOf(
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        return try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayName = cursor.getString(
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    ) ?: "Image"

                    // Load inference from sidecar file
                    val inferenceData = loadInferenceMetadata(displayName)
                    Log.d(tag, "getImageDetails: loaded inferenceData=$inferenceData for $displayName")

                    DamageImageItem(
                        uri = uri,
                        displayName = displayName,
                        dateAddedSeconds = cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                        ),
                        inferenceData = inferenceData
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.e(tag, "getImageDetails failed: ${e.message}")
            null
        }
    }
}
