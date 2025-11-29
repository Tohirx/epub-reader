package com.tohir.booksplusplus.data.model


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.mediatype.MediaType
import java.io.Serializable

@Entity(
    indices = [Index(value = [Bookmark.ID])],
    tableName = "bookmark",
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = [Book.ID],
        childColumns = [Bookmark.BOOK_ID],
        onDelete = CASCADE
    )]
)
data class Bookmark(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    var id: Long = 0,
    @ColumnInfo(name = CREATION_DATE, defaultValue = "CURRENT_TIMESTAMP")
    var creation: String? = null,
    @ColumnInfo(name = BOOK_ID)
    val bookId: Long,
    @ColumnInfo(name = Highlight.HREF)
    var href: String,
    @ColumnInfo(name = TITLE, defaultValue = "NULL")
    var title: String? = null,
    @ColumnInfo(name = TOTAL_PROGRESSION, defaultValue = "0")
    var totalProgression: Double = 0.0,
    @ColumnInfo(name = LOCATIONS, defaultValue = "{}")
    var locations: Locator.Locations = Locator.Locations(),
    @ColumnInfo(name = TEXT, defaultValue = "{}")
    var text: Locator.Text = Locator.Text(),
    @ColumnInfo(name = Highlight.TYPE)
    var type: String,
): Serializable {

    constructor(
        bookId: Long,
        locator: Locator,
        date: String
    ) :
            this(
                href = locator.href.toString(),
                bookId = bookId,
                title = locator.title,
                totalProgression = locator.locations.totalProgression ?: 0.0,
                locations = locator.locations,
                text = locator.text,
                type = locator.mediaType.toString(),
                creation = date
            )

    val locator: Locator
        get() = Locator(
            href = Url(href)!!,
            mediaType = MediaType(type) ?: MediaType.BINARY,
            title = title,
            locations = locations,
            text = text
        )

    companion object {
        const val ID = "ID"
        const val CREATION_DATE = "CREATION_DATE"
        const val BOOK_ID = "BOOK_ID"

        const val TITLE = "TITLE"
        const val TOTAL_PROGRESSION = "TOTAL_PROGRESSION"
        const val LOCATIONS = "LOCATIONS"
        const val TEXT = "TEXT"
    }
}
