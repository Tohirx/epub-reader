package com.tohir.booksplusplus.data

import android.support.annotation.ColorInt
import com.tohir.booksplusplus.data.database.BookDao
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.data.model.Note
import kotlinx.coroutines.flow.Flow
import org.readium.r2.shared.publication.Locator

class BooksRepository(private val bookDao: BookDao) {

    suspend fun addBook(book: Book) {
        bookDao.addBook(book)
    }

    suspend fun getBookIdByHash(hash: String): Long? {
        return bookDao.getBookIdByHash(hash)
    }

     fun getFinishedBooks(): Flow<List<Book>> {
        return bookDao.getFinishedBooks()
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
        date: String
    )  { bookDao.addHighlight(Highlight(bookId, style, tint, locator, annotation, date)) }

    fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooksAsFlow()

    suspend fun saveReadingProgress(locator: String?, bookID: Long?) {
        bookDao.saveReadingProgress(locator, bookID)
    }

    suspend fun existByHash(hash: String): Boolean {
        return bookDao.existsByHash(hash)
    }


    suspend fun getReadingProgress(bookID: Long): String? {
        return bookDao.getReadingProgress(bookID)
    }

    suspend fun addBookmark(bookmark: Bookmark) {
        bookDao.addBookmark(bookmark)
    }

    suspend fun deleteBookmark(id: Long) {
        bookDao.deleteBookmarkById(id)
    }

     fun getAllBookmarks(bookID: Long): Flow<List<Bookmark>> {
        return bookDao.getAllBookmarks(bookID)
    }

    fun getRecentBooks(): Flow<List<Book>> {
        return bookDao.getRecentBooks()
    }


    suspend fun findNoteById(id: Long): Note {
        return bookDao.findNoteById(id)
    }

    suspend fun addNote(note: Note) = bookDao.addNote(note)

     fun getAllNotes(bookId: Long) : Flow<List<Note>> {
        return bookDao.getAllNotes(bookId)
    }

    suspend fun updateNote(note: Note) {
        bookDao.updateNote(note)
    }

    suspend fun deleteNoteById(id: Long) {
        bookDao.deleteNoteById(id)
    }

    suspend fun findBookById(bookId: Long): Book {
        return bookDao.findBookById(bookId)

    }


}