package pl.szkolaandroida.spy

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LocationUploadWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("TAG", "doWork!")
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "location-database"
        ).build()
        val locations = db.locationDao().allLocations

        val api = (applicationContext as App).locationApi

        // Assuming you have a Retrofit instance to upload the locations
        //val apiService: ApiService = RetrofitInstance.apiService

        try {
            for (location in locations) {
                Log.d("TAG", "Location to send: $location")
                val body = LocationInfo(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = location.timestamp,
                    networkInfo = "TODO"
                )
                val response = api.postLocation(
                    body
                ).execute()
                if (response.isSuccessful) {
                    Log.d("TAG", "Sent: $body")
                    // Optionally delete location after successful upload
                    db.locationDao().delete(location)
                }
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("TAG", "error", e)
            return Result.failure()
        }
    }
}
