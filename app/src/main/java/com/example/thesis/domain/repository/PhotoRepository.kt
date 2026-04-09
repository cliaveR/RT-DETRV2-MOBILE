package com.example.thesis.domain.repository

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.OutputStream
import kotlin.coroutines.resume

class PhotoRepository(private val context: Context) {

    private val client = OkHttpClient()

    // 🔥 1. Upload + save → return Uri
    suspend fun uploadAndSaveVisualized(uri: Uri): Uri? =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                    ?: throw Exception("Could not read image")
                inputStream.close()

                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart(
                        "image",
                        "thesis_photo.jpg",
                        bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    .build()

                val request = Request.Builder()
                    .url("http://10.0.2.2:8080/api/upload/visualize")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: java.io.IOException) {
                        e.printStackTrace()
                        Log.d("PICTURE", "Upload failed: ${e.message}")
                        if (continuation.isActive) continuation.resume(null)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            Log.d("PICTURE", "Response code: ${it.code}")

                            if (!it.isSuccessful) {
                                Log.d("PICTURE", "Error: ${it.body?.string()}")
                                continuation.resume(null)
                                return
                            }

                            val imageBytes = it.body?.bytes()

                            if (imageBytes != null) {
                                val savedUri = saveImageToGallery(imageBytes)
                                continuation.resume(savedUri)
                            } else {
                                Log.d("PICTURE", "Image bytes NULL")
                                continuation.resume(null)
                            }
                        }
                    }
                })

            } catch (e: Exception) {
                Log.d("PICTURE", "Exception: ${e.message}")
                if (continuation.isActive) continuation.resume(null)
            }
        }

    // 🔥 2. Save image → return Uri
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

            val outputStream: OutputStream? =
                context.contentResolver.openOutputStream(uri)

            outputStream?.use {
                it.write(bytes)
                it.flush()
            }

            uri // ✅ return URI (IMPORTANT)
        } catch (e: Exception) {
            Log.d("PICTURE", "Save failed: ${e.message}")
            null
        }
    }

    // 🔥 3. Get latest image from your folder
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
}