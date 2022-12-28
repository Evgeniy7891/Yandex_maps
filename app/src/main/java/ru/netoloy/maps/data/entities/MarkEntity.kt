package ru.netoloy.maps.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netoloy.maps.model.MarkMap
import com.yandex.mapkit.geometry.Point


@Entity
data class MarkEntity(
    @PrimaryKey
    val id: Long,
    val name: String,
    val pointLat: Double,
    val pointLon: Double
) {
    fun toDto() = MarkMap(
        id,
        name,
        Point(pointLat, pointLon)
    )
    companion object {
        fun fromDto(dto: MarkMap) =
            MarkEntity(
                dto.id, dto.name, dto.point.latitude, dto.point.longitude
            )
    }
}
