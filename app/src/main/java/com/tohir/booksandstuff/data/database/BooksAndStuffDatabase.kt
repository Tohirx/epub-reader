package com.tohir.booksandstuff.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.data.database.BookDao

@Database(entities = [Book::class], version = 2)
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