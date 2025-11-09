package com.tohir.booksplusplus.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.data.model.Highlight
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

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

    @Query("SELECT readingProgressDouble FROM book WHERE id = :bookID")
    suspend fun getReadingProgressDouble(bookID: Long): Double

    @Query("SELECT * FROM highlight WHERE BOOK_ID = :bookID")
    fun getAllHighlights(bookID: Long): Flow<List<Highlight>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHighlight(highlight: Highlight)

    @Query("SELECT * FROM book")
    suspend fun getAllBooksAsList(): List<Book>

    @Query("SELECT * FROM book WHERE id = :id")
    fun getBookById(id: Long): Book

    @Query("SELECT * FROM book ORDER BY lastDateOpened DESC LIMIT 5")
    fun getRecentBooks(): Flow<List<Book>>


}