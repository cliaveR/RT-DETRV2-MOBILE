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
                .url("http://192.168.254.201:8080/api/upload/visualize")
                .post(multipartBuilder.build())
                .build()

            // Start timer right before the network call
            val uploadStartMs = System.currentTimeMillis()
            Log.d(tag, "Upload started at $uploadStartMs")

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.e(tag, "Upload failed network error: ${e.message}")
                    if (continuation.isActive) continuation.resume(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    // Stop timer as soon as response arrives
                    val clientRoundTripMs = (System.currentTimeMillis() - uploadStartMs).toInt()
                    Log.d(tag, "Response received — client round-trip=${clientRoundTripMs}ms")

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
                            val imageBase64 = json.optString("imageBase64")
                            val latitude = json.optDouble("latitude", Double.NaN).takeIf { !it.isNaN() }
                            val longitude = json.optDouble("longitude", Double.NaN).takeIf { !it.isNaN() }

                            // Use server processingTimeMs if available, else client round-trip
                            val serverProcessingTimeMs = json.optInt("processingTimeMs", -1)
                            val processingTimeMs = if (serverProcessingTimeMs >= 0) {
                                Log.d(tag, "Using server processing time: ${serverProcessingTimeMs}ms")
                                serverProcessingTimeMs
                            } else {
                                Log.d(tag, "Using client round-trip: ${clientRoundTripMs}ms")
                                clientRoundTripMs
                            }

                            Log.d(tag, "Final processingTimeMs=$processingTimeMs")
                            Log.d(tag, "Received inferenceData from server: $inferenceData")

                            val savedUri = if (!imageBase64.isNullOrBlank()) {
                                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                                saveImageToGallery(imageBytes, inferenceData, processingTimeMs)
                            } else null

                            if (continuation.isActive) {
                                continuation.resume(
                                    PhotoUploadResult(
                                        frameId = frameId,
                                        processingTimeMs = processingTimeMs,
                                        inferenceData = inferenceData,
                                        savedImageUri = savedUri,
                                        latitude = latitude,
                                        longitude = longitude
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            Log.e(tag, "Failed to parse response: ${e.message}")
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(tag, "Upload exception: ${e.message}")
            if (continuation.isActive) continuation.resume(null)
        }
    }

    private fun saveImageToGallery(
        bytes: ByteArray,
        inferenceData: String? = null,
        processingTimeMs: Int? = null
    ): Uri? {
        return try {
            val timestamp = System.currentTimeMillis()
            val filename = "Thesis_Processed_${timestamp}.jpg"

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ThesisApp")
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return null

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(bytes)
                outputStream.flush()
            }

            if (!inferenceData.isNullOrBlank()) {
                saveInferenceMetadata(timestamp, inferenceData, processingTimeMs)
            }

            Log.d(tag, "Saved image to gallery, metadata saved for timestamp: $timestamp")
            uri
        } catch (e: Exception) {
            Log.e(tag, "Failed to save image: ${e.message}")
            null
        }
    }

    private fun saveInferenceMetadata(
        timestamp: Long,
        inferenceData: String,
        processingTimeMs: Int? = null
    ) {
        try {
            val metaDir = java.io.File(context.filesDir, "inference_metadata")
            if (!metaDir.exists()) metaDir.mkdirs()
            val metaFile = java.io.File(metaDir, "meta_${timestamp}.json")
            val meta = JSONObject().apply {
                put("inferenceData", inferenceData)
                processingTimeMs?.let { put("processingTimeMs", it) }
            }
            metaFile.writeText(meta.toString())
            Log.d(tag, "Inference metadata saved: ${metaFile.absolutePath}")
        } catch (e: Exception) {
            Log.e(tag, "Failed to save inference metadata: ${e.message}")
        }
    }

    private fun loadInferenceMetadata(displayName: String): Pair<String?, Int?> {
        return try {
            val timestamp = displayName
                .removePrefix("Thesis_Processed_")
                .removeSuffix(".jpg")
                .toLongOrNull() ?: return Pair(null, null)

            val metaDir = java.io.File(context.filesDir, "inference_metadata")
            val metaFile = java.io.File(metaDir, "meta_${timestamp}.json")
            if (!metaFile.exists()) return Pair(null, null)

            val meta = JSONObject(metaFile.readText())
            val inferenceData = meta.optString("inferenceData").ifBlank { null }
            val processingTimeMs = if (meta.has("processingTimeMs")) meta.getInt("processingTimeMs") else null
            Pair(inferenceData, processingTimeMs)
        } catch (e: Exception) {
            Log.e(tag, "Failed to load inference metadata: ${e.message}")
            Pair(null, null)
        }
    }

    fun getDamageImagesFromGallery(): List<DamageImageItem> {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
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
                val (inferenceData, processingTimeMs) = loadInferenceMetadata(displayName)

                results += DamageImageItem(
                    uri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(idIndex).toString()
                    ),
                    displayName = displayName,
                    dateAddedSeconds = cursor.getLong(dateIndex),
                    inferenceData = inferenceData,
                    processingTimeMs = processingTimeMs
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

                    val (inferenceData, processingTimeMs) = loadInferenceMetadata(displayName)
                    Log.d(tag, "getImageDetails: inferenceData=$inferenceData processingTimeMs=$processingTimeMs for $displayName")

                    DamageImageItem(
                        uri = uri,
                        displayName = displayName,
                        dateAddedSeconds = cursor.getLong(
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                        ),
                        inferenceData = inferenceData,
                        processingTimeMs = processingTimeMs
                    )
                } else null
            }
        } catch (e: Exception) {
            Log.e(tag, "getImageDetails failed: ${e.message}")
            null
        }
    }
}