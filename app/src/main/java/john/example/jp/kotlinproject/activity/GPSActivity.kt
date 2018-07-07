package john.example.jp.kotlinproject.activity

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.location.LocationManager
import android.util.Log
import john.example.jp.kotlinproject.R
import kotlinx.android.synthetic.main.activity_gps.*


class GPSActivity: AppCompatActivity(), LocationListener {

    private var locationManager: LocationManager? = null

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0f, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager?.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        Log.d("onLocationChanged", "latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude());
        gps_test.text = "latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        // 下のenabled and disabledのやつ
    }

    override fun onProviderEnabled(p0: String?) {
        // ネットワークからの復旧をした
    }

    override fun onProviderDisabled(p0: String?) {
        // ネットワークから切れた
    }
}