package com.tohir.booksplusplus.ui.books.publicationparser

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.tohir.booksplusplus.util.BooksPlusPlus.Companion.booksRepository
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.Asset
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.shared.util.toAbsoluteUrl
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser

class PublicationParser {
    companion object {
        suspend fun retrievePublication(context: Context, bookId: Long): Publication? {
            var publication: Publication? = null

            val book = booksRepository.findBookById(bookId)
            val httpClient = DefaultHttpClient()
            val assetRetriever = AssetRetriever(context.contentResolver, httpClient)
            val url: AbsoluteUrl? = book.uri.toUri().toAbsoluteUrl()

            val asset: Asset? = url?.let { assetRetriever.retrieve(url).getOrNull() }

            if (asset != null) {
                val publicationParser = DefaultPublicationParser(
                    context,
                    httpClient,
                    assetRetriever,
                    null
                )
                val publicationOpener = PublicationOpener(publicationParser)

                publication =
                    publicationOpener.open(asset, allowUserInteraction = false).getOrNull()

            }
            return publication
        }

        suspend fun parsePublication(context: Context, uri: Uri): Publication? {
            var publication: Publication? = null
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
                    null
                )

                val publicationOpener = PublicationOpener(publicationParser)

                val publicationResult = publicationOpener.open(asset, allowUserInteraction = true)

                if (publicationResult.isSuccess) {
                    publication = publicationResult.getOrNull()
                } else {
                    Toast.makeText(context, "File appears to be corrupted...", Toast.LENGTH_LONG)
                        .show()
                    Log.d(
                        "publication",
                        publicationResult.failureOrNull()?.message + publicationResult.failureOrNull()?.cause
                    )
                }
            }
           return publication
        }

    }
}