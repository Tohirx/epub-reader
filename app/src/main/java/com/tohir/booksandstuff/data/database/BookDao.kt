package com.tohir.booksandstuff.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tohir.booksandstuff.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Query("UPDATE book SET readingProgressJSON = :locator WHERE id = :bookID")
    suspend fun saveReadingProgress(locator: String?, bookID: Int?)

    @Query("SELECT readingProgressJSON from book WHERE id = :bookID")
    suspend fun getReadingProgress(bookID: Int?): String?

    @Query("UPDATE book SET readingProgressDouble = :value WHERE id = :bookID")
    suspend fun saveReadingProgressAsDouble(value: Double, bookID: Int)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM book ORDER BY dateAdded DESC")
    fun getAllBooksAsFlow(): Flow<List<Book>>

    @Query("SELECT readingProgressDouble FROM book WHERE id = :bookID")
    suspend fun getReadingProgressDouble(bookID: Int): Double

    @Query("SELECT * FROM book WHERE identifier = :identifier LIMIT 1")
    suspend fun getBookByIdentifier(identifier: String?): Book?

    @Query("SELECT * FROM book")
    suspend fun getAllBooksAsList(): List<Book>

    @Query("SELECT * FROM book WHERE id = :id")
    fun getBookById(id: Int): Book

    @Query("SELECT * FROM book ORDER BY lastDateOpened DESC LIMIT 5")
    fun getRecentBooks(): Flow<List<Book>>


}