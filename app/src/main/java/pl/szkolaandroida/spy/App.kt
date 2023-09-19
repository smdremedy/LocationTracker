package pl.szkolaandroida.spy

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class App : Application() {

    val locationApi: LocationApi by lazy {
        val retrofit = Retrofit.Builder()
            .addConverterFactory((GsonConverterFactory.create()))
            .baseUrl("https://parseapi.back4app.com")
            .build()

        return@lazy retrofit.create<LocationApi?>(LocationApi::class.java)
    }

    override fun onCreate() {
        super.onCreate()
        val uploadWorkRequest =
            PeriodicWorkRequestBuilder<LocationUploadWorker>(15, TimeUnit.MINUTES)
                .setInitialDelay(0, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance(this).enqueue(uploadWorkRequest)
    }
}