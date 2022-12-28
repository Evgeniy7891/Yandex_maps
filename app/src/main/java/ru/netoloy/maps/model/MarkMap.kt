package ru.netoloy.maps.model

data class MarkMap(
    val id: Long,
    val name: String,
    val point: com.yandex.mapkit.geometry.Point
)
