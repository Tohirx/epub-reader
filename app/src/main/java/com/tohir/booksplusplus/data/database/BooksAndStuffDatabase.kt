package com.tohir.booksplusplus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tohir.booksplusplus.data.model.Book

@Database(entities = [Book::class], version = 3)
abstract class BooksAndStuffDatabase : RoomDatabase() {

    abstract fun getBookDao(): BookDao
    companion object {

        @Volatile
        private var DATABASE_INSTANCE: BooksAndStuffDatabase? = null

        fun createDatabase(context: Context): BooksAndStuffDatabase {

            return DATABASE_INSTANCE ?: synchronized(this) {
                DATABASE_INSTANCE = Room.databaseBuilder(context.applicationContext, BooksAndStuffDatabase::class.java, "books-and-stuff-data")
                    .fallbackToDestructiveMigration(dropAllTables = true )
                    .build()


                DATABASE_INSTANCE!!
            }
        }

    }




}