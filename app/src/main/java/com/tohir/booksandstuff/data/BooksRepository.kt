package com.tohir.booksandstuff.data

import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.data.database.BookDao
import kotlinx.coroutines.flow.Flow

class BooksRepository(private val bookDao: BookDao) {

    suspend fun addBook(book: Book) {
        bookDao.addBook(book)
    }

    suspend fun deleteBook(book: Book) {
        bookDao.deleteBook(book)
    }

    suspend fun updateBook(book: Book) {
        bookDao.updateBook(book)
    }

     fun getAllBooks(): Flow<List<Book>> = bookDao.getAllBooks()

}