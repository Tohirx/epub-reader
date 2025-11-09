package com.tohir.booksplusplus.data

import android.support.annotation.ColorInt
import com.tohir.booksplusplus.data.database.BookDao
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.data.model.Highlight
import kotlinx.coroutines.flow.Flow
import org.readium.r2.shared.publication.Locator

class BooksRepository(private val bookDao: BookDao) {

    suspend fun addBook(book: Book) {
        bookDao.addBook(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }

    suspend fun saveReadingProgressAsDouble(value: Double, bookID: Long) {
        bookDao.saveReadingProgressAsDouble(value, bookID)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun deleteHighlightById(id: Long) {
        bookDao.deleteHighlightById(id)
    }

    fun getAllHighlights(bookID: Long): Flow<List<Highlight>> {
        return bookDao.getAllHighlights(bookID)
    }

    suspend fun updateHighlight(highlight: Highlight) {
        bookDao.updateHighlight(highlight)
    }

    suspend fun findHighlightById(id: Long): Highlight {
        return bookDao.findHighlightById(id)
    }

    suspend fun addHighlight(
        bookId: Long,
        style: Highlight.Style,
        @ColorInt tint: Int,
        locator: Locator,
        annotation: String,
    ) = bookDao.addHighlight(Highlight(bookId, style, tint, locator, annotation))

    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooksAsFlow()

    suspend fun saveReadingProgress(locator: String?, bookID: Long?) {
        bookDao.saveReadingProgress(locator, bookID)
    }

    suspend fun getReadingProgressAsDouble(bookID: Long): Double {
        return bookDao.getReadingProgressDouble(bookID)
    }

    suspend fun getReadingProgress(bookID: Long): String? {
        return bookDao.getReadingProgress(bookID)
    }


    suspend fun getAllBooksAsList(): List<Book> {
        return bookDao.getAllBooksAsList()
    }

    suspend fun getBookById(id: Long): Book {
        return bookDao.getBookById(id)
    }

    fun getRecentBooks(): Flow<List<Book>> {
        return bookDao.getRecentBooks()
    }


}