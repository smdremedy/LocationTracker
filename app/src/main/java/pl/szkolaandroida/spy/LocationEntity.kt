package pl.szkolaandroida.spy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)