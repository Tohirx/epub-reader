package com.tohir.booksplusplus.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["identifier"], unique = true)])
data class Book(

     @PrimaryKey (autoGenerate = true) val id: Long = 0,
    val title: String?,
    val lastDateOpened: String?,
    val dateAdded: Long = 0,
    val author: String?,
    val cover: String?,
    val uri: String,
    val identifier: String?,
    val readingProgressJSON: String?,
    val readingProgressDouble: Double?,
    val mediaType: String?,
    val isComplete: Boolean = false,
    val isFavourite: Boolean = false,
    val wantToRead: Boolean = false
) {
    companion object {
        const val ID = "id"
    }

}
