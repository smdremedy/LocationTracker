package pl.szkolaandroida.spy

import com.google.gson.GsonBuilder
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(locationNetworkService: LocationNetworkService)

    fun getLocationApi(): LocationApi
}

@Module
class AppModule() {

    @Singleton
    @Provides
    fun providesLocationApi(retrofit: Retrofit): LocationApi {
        return retrofit.create<LocationApi?>(LocationApi::class.java)
    }

    @Provides
    fun providesRetrofit(@Named(URL) url: String): Retrofit {
        val gson = GsonBuilder().create()
        val retrofit = Retrofit.Builder()
            .addConverterFactory((GsonConverterFactory.create(gson)))
            .baseUrl(url)
            .build()
        return retrofit
    }

    @Provides
    @Named(URL)
    fun providesUrl() = "https://parseapi.back4app.com"

    @Provides
    fun providesApiKey() = "KEY"

    companion object {
        private const val URL = "URL"
    }

}