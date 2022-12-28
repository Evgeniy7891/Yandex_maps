package ru.netoloy.maps.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netoloy.maps.data.entities.MarkEntity

@Dao
interface MarkDao {
    @Query("SELECT * FROM MarkEntity")
    fun getAll(): LiveData<List<MarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(markPoint: MarkEntity)

    @Query("DELETE FROM MarkEntity WHERE id = :id")
    fun removeById(id: Long)
}