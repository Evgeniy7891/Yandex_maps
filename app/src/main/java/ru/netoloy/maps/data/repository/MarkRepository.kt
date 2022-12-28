package ru.netoloy.maps.data.repository

import androidx.lifecycle.LiveData
import ru.netoloy.maps.model.MarkMap

interface MarkRepository {
    fun getAll():LiveData<List<MarkMap>>
    fun insert(markMap:MarkMap)
    fun removeById(id: Long)
}