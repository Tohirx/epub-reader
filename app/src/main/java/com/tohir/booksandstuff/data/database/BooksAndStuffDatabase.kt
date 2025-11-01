package com.tohir.booksandstuff.data.database

import android.R.attr.version
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.data.database.BookDao
import com.tohir.booksandstuff.data.model.Highlight
import com.tohir.booksandstuff.data.model.HighlightConverters

@Database(entities = [Book::class, Highlight::class], version = 4)
@TypeConverters(HighlightConverters::class)

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