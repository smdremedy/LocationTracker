package pl.szkolaandroida.spy

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun start(view: View) {
        requestLocationPermission()
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 1234 // Any unique integer


    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf<String>(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission is already granted, you can go ahead and access the location
            // startLocationUpdates(); or any other method you'd like to execute
            spy()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                // startLocationUpdates(); or any other method you'd like to execute
                spy()
            } else {
                // Permission was denied. Handle the denial gracefully, perhaps inform the user that the feature won't work without the permission.
                Toast.makeText(
                    this,
                    "Location permission is required for this feature",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        // Handle other permission results you might have
    }


    fun spy() {
        val serviceIntent = Intent(this, LocationNetworkService::class.java)
        startService(serviceIntent)
    }

}