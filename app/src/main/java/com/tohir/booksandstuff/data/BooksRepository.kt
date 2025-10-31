package com.tohir.booksandstuff.data

import android.util.Log.i
import com.tohir.booksandstuff.data.database.BookDao
import com.tohir.booksandstuff.data.model.Book
import kotlinx.coroutines.flow.Flow

class BooksRepository(private val bookDao: BookDao) {

    suspend fun addBook(book: Book) {
        bookDao.addBook(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }

    suspend fun saveReadingProgressAsDouble(value: Double, bookID: Int) {
        bookDao.saveReadingProgressAsDouble(value, bookID)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooksAsFlow()

    suspend fun saveReadingProgress(locator: String?, bookID: Int?) {
        bookDao.saveReadingProgress(locator, bookID)
    }

    suspend fun getReadingProgressAsDouble(bookID: Int): Double {
        return bookDao.getReadingProgressDouble(bookID)
    }

    suspend fun getReadingProgress(bookID: Int): String? {
        return bookDao.getReadingProgress(bookID)
    }

    suspend fun getBookByIdentifier(identifier: String?): Book? {
        return bookDao.getBookByIdentifier(identifier = identifier)
    }

    suspend fun getAllBooksAsList(): List<Book> {
        return bookDao.getAllBooksAsList()
    }

    suspend fun getBookById(id: Int): Book {
        return bookDao.getBookById(id)
    }

    fun getRecentBooks(): Flow<List<Book>> {
        return bookDao.getRecentBooks()
    }






}