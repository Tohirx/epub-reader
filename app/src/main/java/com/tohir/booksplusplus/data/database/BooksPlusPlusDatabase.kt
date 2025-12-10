package com.tohir.booksplusplus.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.data.model.HighlightConverters
import com.tohir.booksplusplus.data.model.Note

@Database(entities = [Book::class, Note::class, Highlight::class, Bookmark::class], version = 17)
@TypeConverters(HighlightConverters::class)

abstract class BooksPlusPlusDatabase : RoomDatabase() {

    abstract fun getBookDao(): BookDao

    companion object {

        @Volatile
        private var DATABASE_INSTANCE: BooksPlusPlusDatabase? = null


        fun createDatabase(context: Context): BooksPlusPlusDatabase {

            return DATABASE_INSTANCE ?: synchronized(this) {
                DATABASE_INSTANCE = Room.databaseBuilder(context.applicationContext, BooksPlusPlusDatabase::class.java, "books-plus-plus-data")
                    .fallbackToDestructiveMigration(true)
                    .build()


                DATABASE_INSTANCE!!
            }
        }

    }

}