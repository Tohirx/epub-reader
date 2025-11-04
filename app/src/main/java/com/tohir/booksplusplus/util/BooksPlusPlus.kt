package com.tohir.booksplusplus.util

import android.app.Application
import com.tohir.booksplusplus.data.BooksRepository
import com.tohir.booksplusplus.data.database.BooksAndStuffDatabase

class BooksPlusPlus: Application() {


    override fun onCreate() {
        super.onCreate()

        val database = BooksAndStuffDatabase.Companion.createDatabase(this)
        val bookDao = database.getBookDao()

        booksRepository = BooksRepository(bookDao)

    }

    companion object {
        lateinit var booksRepository: BooksRepository

    }
}