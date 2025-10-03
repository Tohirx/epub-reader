package com.tohir.booksandstuff.data.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book (

    @PrimaryKey(autoGenerate = true) val bookId: Int = 0,
    val lastOpenedPage: Int = 0,
    val title: String?,
    val dateAdded: Long = 0,
    val author: String?,
    val cover: String?
)