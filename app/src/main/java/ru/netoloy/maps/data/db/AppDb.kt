package ru.netoloy.maps.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.netoloy.maps.data.dao.MarkDao
import ru.netoloy.maps.data.entities.MarkEntity

@Database(entities =[MarkEntity::class], version = 1)
abstract class AppDb : RoomDatabase() {
abstract fun markDao() : MarkDao

    companion object {
        @Volatile
        private var instance: AppDb? = null

        fun getInstance(context: Context): AppDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDb::class.java, "app.db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
    }
}