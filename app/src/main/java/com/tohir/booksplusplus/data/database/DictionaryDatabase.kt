package com.tohir.booksplusplus.data.database

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Database(entities = [DummyEntity::class], version = 2)
abstract class DictionaryDatabase : RoomDatabase() {
    abstract fun dictionaryDao(): DictionaryDao
}

@Entity(tableName = "dummy")
data class DummyEntity(
    @PrimaryKey val id: Int = 0
)