package pl.szkolaandroida.spy

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.room.Room.databaseBuilder
import java.util.Date
import javax.inject.Inject
import kotlin.concurrent.thread


/**
 * https://dontkillmyapp.com/
 */
class LocationNetworkService : Service() {
    private lateinit var locationManager: LocationManager
    private var connectivityManager: ConnectivityManager? = null
    private var wakeLock: WakeLock? = null
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var db: AppDatabase

    @Inject
    lateinit var locationApi: LocationApi
    @Inject
    lateinit var sessionManager: SessionManager

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                // Handle the location data
                Log.d("TAG", "Updated locaiton: $location")
                thread {
                    db.locationDao().insert(
                        LocationEntity(
                            latitude = latitude,
                            longitude = longitude,
                            timestamp = Date().time
                        )
                    )
                }

            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    var phoneStateListener: PhoneStateListener = object : PhoneStateListener() {
        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            super.onSignalStrengthsChanged(signalStrength)
            Log.d("TAG", "Strength: $signalStrength")
        }
    }


    override fun onCreate() {
        super.onCreate()

        App.component.inject(this)

        db = databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "location-database"
        ).build()
        createNotificationChannel()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MyApp::MyWakelockTag"
        )

        wakeLock?.acquire()
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.release()
    }

    private val activeNetworkInfo: NetworkInfo?
        private get() = connectivityManager!!.activeNetworkInfo
//    private val lastKnownLocation: Location?
//        private get() = if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            null
//        } else locationManager?.get

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        startForeground(1, notification)

        //handler.post(runnable)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                15000L,
                0f,
                locationListener
            );

        }

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        // Handle your tasks here
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID, "Location and Network Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private val notification: Notification
        private get() = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location and Network Service")
            .setContentText("Fetching location and network state...") // You can set an icon here
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

    companion object {
        // Service implementation
        private const val CHANNEL_ID = "LocationNetworkServiceChannel"
    }
}