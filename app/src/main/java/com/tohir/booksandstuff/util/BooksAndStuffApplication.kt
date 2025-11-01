package com.tohir.booksandstuff.util

import android.app.Application
import com.squareup.picasso.Picasso
import com.tohir.booksandstuff.data.BooksRepository
import com.tohir.booksandstuff.data.database.BooksAndStuffDatabase
import org.readium.r2.shared.publication.Publication

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