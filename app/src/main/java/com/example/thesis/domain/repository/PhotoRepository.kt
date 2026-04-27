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
import kotlin.coroutines.resume

class PhotoRepository(private val context: Context) {

    private val client = OkHttpClient()
    private val tag = "PICTURE"

    suspend fun uploadAndSaveVisualized(
        uri: Uri,
        captureCoordinate: GeoCoordinate? = null
    ): PhotoUploadResult? = suspendCancellableCoroutine { continuation ->
        try {
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
                Log.d(tag, "Bundled photo GPS lat=${it.latitude}, lng=${it.longitude}")
            }

            val latitudeText = captureCoordinate?.latitude?.toString() ?: "null"
            val longitudeText = captureCoordinate?.longitude?.toString() ?: "null"
            val imageSizeKb = bytes.size / 1024.0

            Log.d(tag, "Sending image file: name=thesis_photo.jpg, size=${"%.2f".format(imageSizeKb)} KB")
            Log.d(
                tag,
                "Sending multipart fields: latitude=$latitudeText, longitude=$longitudeText, image=thesis_photo.jpg"
            )

            val requestBody = multipartBuilder.build()

            val request = Request.Builder()
                .url("http://10.0.2.2:8080/upload/visualize")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.d(tag, "Upload failed: ${e.message}")
                    if (continuation.isActive) continuation.resume(null)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        val responseText = it.body?.string()
                        Log.d(tag, "Response code: ${it.code}")
                        Log.d(tag, "Raw response: $responseText")

                        if (!it.isSuccessful) {
                            Log.d(tag, "Error: $responseText")
                            if (continuation.isActive) continuation.resume(null)
                            return
                        }

                        try {
                            if (responseText == null) {
                                if (continuation.isActive) continuation.resume(null)
                                return
                            }

                            val json = JSONObject(responseText)

                            val frameId: String? = json.optString("frameId").takeIf { it.isNotBlank() }
                            val inferenceData: String? = json.optString("inferenceData").takeIf { it.isNotBlank() }
                            val processingTimeMs = json.optInt("processingTimeMs", -1)
                            val imageBase64: String? = json.optString("imageBase64").takeIf { it.isNotBlank() }

                            Log.d(tag, "FrameId = $frameId")
                            Log.d(tag, "Processing time ms = $processingTimeMs")
                            Log.d(tag, "Inference data = $inferenceData")

                            val savedUri = if (imageBase64 != null) {
                                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                                saveImageToGallery(imageBytes)
                            } else {
                                null
                            }

                            if (continuation.isActive) {
                                continuation.resume(
                                    PhotoUploadResult(
                                        frameId = frameId,
                                        processingTimeMs = if (processingTimeMs >= 0) processingTimeMs else null,
                                        inferenceData = inferenceData,
                                        savedImageUri = savedUri
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            Log.d(tag, "JSON parse failed: ${e.message}")
                            if (continuation.isActive) continuation.resume(null)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.d(tag, "Exception: ${e.message}")
            if (continuation.isActive) continuation.resume(null)
        }
    }

    private fun saveImageToGallery(bytes: ByteArray): Uri? {
        return try {
            val filename = "Thesis_Processed_${System.currentTimeMillis()}.jpg"
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ThesisApp")
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ) ?: return null

            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            outputStream?.use {
                it.write(bytes)
                it.flush()
            }
            uri
        } catch (e: Exception) {
            Log.d(tag, "Save failed: ${e.message}")
            null
        }
    }

    fun getLatestImageFromGallery(): Uri? {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("%Pictures/ThesisApp%")
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getLong(0)
                return Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
            }
        }
        return null
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

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (it.moveToNext()) {
                val id = it.getLong(idIndex)
                val displayName = it.getString(nameIndex) ?: "Processed Image"
                val dateAddedSeconds = it.getLong(dateIndex)

                results += DamageImageItem(
                    uri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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