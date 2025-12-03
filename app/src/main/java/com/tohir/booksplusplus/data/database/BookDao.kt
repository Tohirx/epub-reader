package com.tohir.booksplusplus.data.database

import android.icu.text.MessagePattern
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.data.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBook(book: Book)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: Bookmark)


    @Query("SELECT * FROM bookmark WHERE bookmark.ID = :id" )
    suspend fun findBookmarkById(id: Long): Bookmark

    @Query("DELETE FROM BOOKMARK WHERE bookmark.ID = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Update
    suspend fun updateBook(book: Book)

    @Query("SELECT * FROM bookmark WHERE bookmark.BOOK_ID = :bookID ORDER BY PAGE_NUMBER ASC")
     fun getAllBookmarks(bookID: Long): Flow<List<Bookmark>>

    @Query("UPDATE book SET readingProgressJSON = :locator WHERE id = :bookID")
    suspend fun saveReadingProgress(locator: String?, bookID: Long?)

    @Query("SELECT readingProgressJSON from book WHERE id = :bookID")
    suspend fun getReadingProgress(bookID: Long?): String?

    @Query("UPDATE book SET readingProgressDouble = :value WHERE id = :bookID")
    suspend fun saveReadingProgressAsDouble(value: Double, bookID: Long)

    @Query("DELETE FROM highlight WHERE ID =:id")
    suspend fun deleteHighlightById(id: Long)

    @Update
    suspend fun updateHighlight(highlight: Highlight)

    @Query("SELECT * FROM highlight WHERE ID = :id")
    suspend fun findHighlightById(id: Long): Highlight

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM book ORDER BY dateAdded DESC")
    fun getAllBooksAsFlow(): Flow<List<Book>>

    @Query("SELECT * FROM highlight ORDER BY ID DESC LIMIT 1")
    suspend fun findLastHighlightAdded(): Highlight

    @Query("SELECT readingProgressDouble FROM book WHERE id = :bookID")
    suspend fun getReadingProgressDouble(bookID: Long): Double

    @Query("SELECT * FROM highlight WHERE BOOK_ID = :bookID ORDER BY PAGE_NUMBER ASC")
    fun getAllHighlights(bookID: Long): Flow<List<Highlight>>

    @Insert(onConflict = REPLACE)
    suspend fun addHighlight(highlight: Highlight)

    @Query("SELECT * FROM book")
    suspend fun getAllBooksAsList(): List<Book>

    @Query("SELECT * FROM book WHERE id = :id")
    fun findBookById(id: Long): Book

    @Query("SELECT * FROM book ORDER BY lastDateOpened DESC LIMIT 5")
    fun getRecentBooks(): Flow<List<Book>>

    @Insert(onConflict = REPLACE)
    suspend fun addNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Query("DELETE FROM note WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Query("SELECT * FROM NOTE WHERE book_id = :bookId")
     fun getAllNotes(bookId: Long): Flow<List<Note>>

    @Query("SELECT * FROM NOTE WHERE id = :id")
    suspend fun findNoteById(id: Long): Note


}