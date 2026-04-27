package com.example.thesis.domain.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.thesis.model.data.mapTracking.GeoCoordinate
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CurrentLocationProvider(private val context: Context) {

    private companion object {
        const val TAG = "LocCapture"
    }

    suspend fun getCurrentCoordinateOrNull(): GeoCoordinate? =
        suspendCancellableCoroutine { continuation ->
            val hasFine = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasFine) {
                Log.w(TAG, "ACCESS_FINE_LOCATION not granted. Returning null coordinate.")
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val client = LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()

            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
                .addOnSuccessListener { location ->
                    if (!continuation.isActive) return@addOnSuccessListener
                    val point = location?.let { GeoCoordinate(it.latitude, it.longitude) }
                    Log.d(TAG, "getCurrentLocation -> $point")
                    continuation.resume(point)
                }
                .addOnFailureListener { error ->
                    Log.e(TAG, "getCurrentLocation failed: ${error.message}")
                    if (continuation.isActive) continuation.resume(null)
                }

            continuation.invokeOnCancellation { cts.cancel() }
        }
}