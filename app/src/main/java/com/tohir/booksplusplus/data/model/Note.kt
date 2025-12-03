package com.tohir.booksplusplus.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.mediatype.MediaType


@Entity(
    tableName = "note",
    indices = [Index(value = ["id"])],
    foreignKeys = [ForeignKey(
        entity = Book::class,
        parentColumns = [Book.ID],
        childColumns = ["book_id"]
    ), ForeignKey(
        entity = Highlight::class,
        parentColumns = [Highlight.ID],
        childColumns = ["highlight_id"]
    )]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val id: Long = 0,
    @ColumnInfo("highlight_id")
    val highlightId: Long = 0,
    @ColumnInfo(name = "book_id")
    val bookId: Long = 0,
    @ColumnInfo("locations")
    var locations: Locator.Locations = Locator.Locations(),
    @ColumnInfo("content")
    val content: String,
    @ColumnInfo("text")
    var text: Locator.Text = Locator.Text(),
    @ColumnInfo("title")
    var title: String? = null,
    @ColumnInfo(name = "href")
    var href: String,
    @ColumnInfo(name = "type")
    var type: String,
) {

    constructor(locator: Locator, content: String, bookId: Long, highlightId: Long) :
            this(
                highlightId = highlightId,
                bookId = bookId,
                locations = locator.locations,
                content = content,
                text = locator.text,
                href = locator.href.toString(),
                title = locator.title,
                type = locator.mediaType.toString()

            )

    val locator: Locator
        get() = Locator(
            href = Url(href)!!,
            mediaType = MediaType(type) ?: MediaType.BINARY,
            title = title,
            locations = locations,
            text = text
        )
}
