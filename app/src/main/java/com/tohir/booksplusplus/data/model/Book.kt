package com.tohir.booksplusplus.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(indices = [Index(value = ["hash"], unique = true)])
data class Book(

     @PrimaryKey (autoGenerate = true) val id: Long = 0,
    val title: String?,
    val lastDateOpened: String? = null,
    val dateAdded: Long = 0,
    val author: String?,
    val cover: String?,
    val uri: String,
    val identifier: String?,
    val readingProgressJSON: String?,
    val readingProgressDouble: Double?,
    val mediaType: String?,
    val isFinished: Boolean = false,
    val isFavourite: Boolean = false,
    val wantToRead: Boolean = false,
    val yearReleased: String? = null,
    val numberOfPages: Int,
    val hash: String
) : java.io.Serializable {
    companion object {
        const val ID = "id"
    }

}
