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
import okio.IOException
import java.io.OutputStream
import kotlin.coroutines.resume

class PhotoRepository(private val context: Context) {

    private val client = OkHttpClient()

    suspend fun uploadAndSaveVisualized(uri: Uri): Boolean =
        suspendCancellableCoroutine { continuation ->
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                    ?: throw IOException("Could not read image")
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
                    .url("http://10.0.2.2:8080/api/upload/visualize") // IMPORTANT
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {

                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace() // 🔥 shows exact error in Logcat
                        Log.d("PICTURE", "${e.printStackTrace()}")
                        if (continuation.isActive) continuation.resume(false)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        response.use {
                            Log.d("PICTURE", "Response code: ${it.code}}")

                            if (!it.isSuccessful) {
                                val errorBody = it.body?.string()
                                Log.d("PICTURE", "$errorBody")

                                // 🔥 backend error message
                            }
                            if (it.isSuccessful) {
                                val imageBytes = it.body?.bytes()
                                Log.d("PICTURE", "Received bytes: ${imageBytes?.size}")
                                if (imageBytes != null) {
                                    val saved = saveImageToGallery(imageBytes)
                                    continuation.resume(saved)
                                } else {
                                    Log.d("PICTURE", "Image bytes are NULL")
                                    continuation.resume(false)
                                }
                            } else {
                                continuation.resume(false)
                            }
                        }
                    }
                })

            } catch (e: Exception) {
                if (continuation.isActive) continuation.resume(false)
            }
        }

    // 🔥 THIS saves image to gallery
    private fun saveImageToGallery(bytes: ByteArray): Boolean {
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
            ) ?: return false

            val outputStream: OutputStream? =
                context.contentResolver.openOutputStream(uri)

            outputStream?.use {
                it.write(bytes)
                it.flush()
            }

            true
        } catch (e: Exception) {
            false
        }
    }
}