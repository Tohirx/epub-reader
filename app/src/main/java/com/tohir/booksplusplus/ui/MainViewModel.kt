package com.tohir.booksplusplus.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tohir.booksplusplus.data.BooksRepository
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.ui.books.reader.ReaderActivity
import com.tohir.booksplusplus.util.BooksPlusPlus
import kotlinx.coroutines.launch
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.cover
import org.readium.r2.shared.publication.services.positions
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.toColorInt

class MainViewModel : ViewModel() {
    private val booksRepository: BooksRepository = BooksPlusPlus.booksRepository
    private var publication: Publication? = null

    suspend fun addBookPublicationToDatabase(uri: Uri, context: Context) {

        val hashedUri = hashUri(context, uri)
        val bookId = booksRepository.getBookIdByHash(hashedUri)

        if (bookId != null) {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.putExtra("BOOK_ID", bookId)
            context.startActivity(intent)
            return
        }


        val httpClient = DefaultHttpClient()
        val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
        val url: AbsoluteUrl? = uri.toAbsoluteUrl()

        val asset = url?.let {
            assetRetriever.retrieve(url).getOrNull()
        }

        if (asset != null) {
            val publicationParser = DefaultPublicationParser(
                context,
                httpClient,
                assetRetriever,
                PdfiumDocumentFactory(context)
            )

            val publicationOpener = PublicationOpener(publicationParser)

            val publicationResult = publicationOpener.open(asset, allowUserInteraction = true)


            if (publicationResult.isSuccess) {
                publication = publicationResult.getOrNull()
            } else {
                Log.d("tohir", "Error importing publication: ${publicationResult.failureOrNull()?.message} ${publicationResult.failureOrNull()?.cause}")
            }


        }

        if (publication != null) {

            val authors = publication!!.metadata.authors.joinToString(", ") { contributor ->
                contributor.name
            }



            // PLEASE FIX THIS LATER. GET A SUITABLE PLACEHOLDER IMAGE FOR BOOKS MISSING A COVER, AND STORE IT THE FILE.
            // This stores the cover in the App's directory as a PNG image

            val cover = if (publication!!.cover() != null) {
                val file = File(context.filesDir, "cover_${publication!!.metadata.identifier}.png")
                if (!file.exists()) {
                    FileOutputStream(file).use {
                        publication!!.cover()!!.compress(Bitmap.CompressFormat.PNG, 90, it)
                    }
                }
                file
            } else {
                generateBookCover(
                    context = context,
                    title = publication!!.metadata.title!!,
                    author = authors,
                    fileName = "cover_generated_${publication!!.metadata.identifier}.png"
                )
            }


            val mediaType = asset?.format?.mediaType
            viewModelScope.launch {

                val year = publication!!.metadata.published?.let { instant ->
                    val date = instant.toJavaDate()
                    val sdf = SimpleDateFormat("yyyy")

                    sdf.format(date)
                }

                val uriFile = copyUriFileToInternalStorage(uri, context)

                if (uriFile != null) {

                    val book = Book(
                        title = publication!!.metadata.title,
                        author = authors,
                        cover = cover.absolutePath,
                        identifier = publication!!.metadata.identifier ?: "",
                        readingProgressJSON = null,
                        mediaType = mediaType.toString(),
                        readingProgressDouble = null,
                        yearReleased = year,
                        numberOfPages = publication!!.positions().size,
                        uri = Uri.fromFile(uriFile).toString(),
                        hash = hashedUri
                    )

                    booksRepository.addBook(book)
                }
            }

        }
    }

    private fun hashUri(context: Context, uri: Uri): String {
        val digest = MessageDigest.getInstance("SHA-256")
        context.contentResolver.openInputStream(uri)?.use { input ->
            val buffer = ByteArray(8 * 1024)
            var bytes = input.read(buffer)
            while (bytes > 0) {
                digest.update(buffer, 0, bytes)
                bytes = input.read(buffer)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    fun generateBookCover(
        context: Context,
        title: String,
        author: String,
        fileName: String
    ): File {

        val width = 1080
        val height = 1440

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        // Background
        canvas.drawColor("#693C00".toColorInt())

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 130f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val authorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.LTGRAY
            textSize = 130f
        }


        val padding = 150f
        var y = height / 3f

        // Title (wrap manually)
        title.split(" ").chunked(3).forEach { line ->
            canvas.drawText(line.joinToString(" "), padding, y, titlePaint)
            y += titlePaint.textSize + 16
        }

        y += 40
        canvas.drawText(author, padding, y, authorPaint)

        val file = File(context.filesDir, fileName)

        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        bitmap.recycle()

        return file
    }



    private fun copyUriFileToInternalStorage(uri: Uri, context: Context): File? {

        try {
            val fileName = "file_${System.currentTimeMillis()}.epub"
            val destinationFile = File(context.filesDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            return destinationFile

        } catch (e: Exception) {
            println(e.message)
            println(e.printStackTrace())

        }

        return null

    }


}