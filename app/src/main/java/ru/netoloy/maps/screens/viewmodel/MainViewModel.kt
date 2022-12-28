package ru.netoloy.maps.screens.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.yandex.mapkit.geometry.Point
import ru.netoloy.maps.data.db.AppDb
import ru.netoloy.maps.data.repository.MarkMapRepositoryImpl
import ru.netoloy.maps.data.repository.MarkRepository
import ru.netoloy.maps.model.MarkMap

class MainViewModel(application: Application) : AndroidViewModel(application){
    private val repository: MarkRepository = MarkMapRepositoryImpl(AppDb.getInstance(application).markDao())
    val data: LiveData<List<MarkMap>> = repository.getAll()
    fun insert(markMap: MarkMap) = repository.insert(markMap)
    fun removeByid(id:Long) = repository.removeById(id)

    var pointPosition : MarkMap? = null
    var beginPosition : Point = Point(54.718928, 55.925596)
}