package com.tohir.booksplusplus.util

import android.app.Application
import com.tohir.booksplusplus.data.BooksRepository
import com.tohir.booksplusplus.data.database.BooksAndStuffDatabase

class BooksAndStuffApplication: Application() {


    override fun onCreate() {
        super.onCreate()

        val database = BooksAndStuffDatabase.createDatabase(this)
        val bookDao = database.getBookDao()

        booksRepository = BooksRepository(bookDao)

    }

    companion object {
        lateinit var booksRepository: BooksRepository

    }
}