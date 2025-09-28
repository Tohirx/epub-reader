package com.tohir.booksandstuff.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book (

    @PrimaryKey(autoGenerate = true) val bookId: Int = 0,
    val lastOpenedPage: Int = 0,
    var title: String,
    var dateAdded: Int = 0,
    var yearOfPublication: Int = 0,
    var author: String,
    var cover: String
)