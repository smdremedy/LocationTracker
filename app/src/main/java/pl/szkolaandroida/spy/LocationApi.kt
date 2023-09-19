package pl.szkolaandroida.spy

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LocationApi {

    @Headers(
        "X-Parse-REST-API-Key: $REST_API_KEY",
        "X-Parse-Application-Id: $APP_ID"
    )
    @POST("classes/LocationInfo")
    fun postLocation(@Body body: LocationInfo): Call<LocationResponse>

    companion object {
        private const val REST_API_KEY = "QojrgHQMn6L1zllg4I9IAnlCYSjOKwzz3EupcnHu\n"
        private const val APP_ID = "CvC5gdCmCX1QckdZcOAvBKty7wz0EreKoIXxeBNO"
    }
}

class LocationResponse {

}

data class LocationInfo(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val networkInfo: String
)