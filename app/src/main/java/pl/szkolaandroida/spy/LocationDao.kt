package pl.szkolaandroida.spy

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {
    @Insert
    fun insert(location: LocationEntity?): Long

    // Add other queries, updates, deletes as needed
    @get:Query("SELECT * FROM locations")
    val allLocations: List<LocationEntity>

    @Delete
    fun delete(location: LocationEntity)

}