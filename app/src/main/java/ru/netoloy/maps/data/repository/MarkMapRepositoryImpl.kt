package ru.netoloy.maps.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.map
import ru.netoloy.maps.data.dao.MarkDao
import ru.netoloy.maps.data.entities.MarkEntity
import ru.netoloy.maps.model.MarkMap

class MarkMapRepositoryImpl(private val dao: MarkDao) : MarkRepository {
    override fun getAll(): LiveData<List<MarkMap>> =
        map(dao.getAll()) {
            it.map {
                it.toDto()
            }
        }

    override fun insert(markMap: MarkMap) {
        dao.insert(MarkEntity.fromDto(markMap))
    }

    override fun removeById(id: Long) {
       dao.removeById(id)
    }
}