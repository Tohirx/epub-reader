package com.tohir.booksandstuff.data.database

import android.content.Context
import androidx.room.Room

object DictionaryProvider {
    @Volatile
    private var INSTANCE: DictionaryDatabase? = null

    fun getInstance(context: Context): DictionaryDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                DictionaryDatabase::class.java,
                "dictionary.db"
            )
                .createFromAsset("dictionary.sqlite")
                .build()
            INSTANCE = instance
            instance
        }
    }
}