package com.tohir.booksandstuff.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.data.database.BookDao

@Database(entities = [Book::class], version = 1)
abstract class BooksAndStuffDatabase : RoomDatabase() {

    abstract fun getBookDao(): BookDao

    companion object {

        @Volatile
        private var DATABASE_INSTANCE: BooksAndStuffDatabase? = null


        fun createDatabase(context: Context): BooksAndStuffDatabase {

            return DATABASE_INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, BooksAndStuffDatabase::class.java, "books-and-stuff-data")
                    .build()


                DATABASE_INSTANCE!!
            }
        }

    }




}