package com.example.thesis.domain.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.thesis.model.data.mapTracking.TrackedLocation
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FusedLocationTrackingService(
    private val context: Context
) : LocationTrackingService {

    private companion object {
        const val TAG = "LocService"
    }

    @SuppressLint("MissingPermission")
    override fun locationUpdates(intervalMs: Long): Flow<TrackedLocation> = callbackFlow {
        Log.i(TAG, "locationUpdates() started. intervalMs=$intervalMs")

        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            Log.e(TAG, "No location permission granted (fine/coarse).")
            close(SecurityException("Location permission not granted"))
            return@callbackFlow
        }

        Log.d(TAG, "Permissions ok. hasFine=$hasFine hasCoarse=$hasCoarse")

        val client = LocationServices.getFusedLocationProviderClient(context)

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            intervalMs
        )
            .setMinUpdateIntervalMillis((intervalMs / 2).coerceAtLeast(500L))
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation ?: return
                Log.d(
                    TAG,
                    "Fused update lat=${loc.latitude}, lng=${loc.longitude}, " +
                            "acc=${loc.accuracy}m, speed=${if (loc.hasSpeed()) loc.speed else "n/a"}, " +
                            "bearing=${if (loc.hasBearing()) loc.bearing else "n/a"}, time=${loc.time}"
                )

                trySend(
                    TrackedLocation(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        accuracyMeters = loc.accuracy,

                        timeStampMs = loc.time
                    )
                )
            }
        }

        client.requestLocationUpdates(request, callback, context.mainLooper)

        awaitClose {
            Log.i(TAG, "locationUpdates() stopped. Removing callbacks.")
            client.removeLocationUpdates(callback)
        }
    }
}