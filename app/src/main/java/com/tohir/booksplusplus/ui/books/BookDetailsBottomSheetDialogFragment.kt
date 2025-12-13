package com.tohir.booksplusplus.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.databinding.FragmentBookDetailsBottomSheetDialogBinding
import kotlin.math.ceil

class BookDetailsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBookDetailsBottomSheetDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookDetailsBottomSheetDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val book = arguments?.getSerializable("book") as? Book

        if (book != null) {

            binding.apply {
                bookAuthor.text = book.author
                bookTitle.text = book.title
                publicationYear.text = "Published ${book.yearReleased}"
                numberOfPages.text = "Number of pages - ${book.numberOfPages}"

                if (book.isFinished) {
                    bookStatus.text = "Finished"
                } else {

                    if (book.lastDateOpened == null) {
                        binding.bookStatus.text = "Not started yet"
                    } else if (book.readingProgressDouble != null && ceil(book.readingProgressDouble * 100).toInt() > 1) {

                        val value = ceil(book.readingProgressDouble * 100).toInt()

                        if (value < 1) {
                            binding.bookStatus.text = "Progress - 1%"
                        } else {
                            val value = ceil(book.readingProgressDouble * 100).toInt()
                            bookStatus.text = "Progress - ${value}%"
                        }

                    } else {
                        bookStatus.text = "Progress - 1%"
                    }

                }
            }

        }

    }

}