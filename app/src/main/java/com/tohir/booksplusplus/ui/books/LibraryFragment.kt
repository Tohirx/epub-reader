package com.tohir.booksplusplus.ui.books

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.databinding.FragmentLibraryBinding
import com.tohir.booksplusplus.ui.books.reader.ReaderActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class LibraryFragment : Fragment(), BookAdapter.BookClickListener {

    private val viewModel: LibraryFragmentViewModel by viewModels()
    private val adapter = BookAdapter(this)
    private lateinit var binding: FragmentLibraryBinding
    private lateinit var books: List<Book>
    private val favouriteBooks = mutableListOf<Book>()
    private val finishedBooks = mutableListOf<Book>()
    private val wantToReadBooks = mutableListOf<Book>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.libraryRecyclerView.adapter = adapter
        fetchAllBooks()

        binding.categoryChipGroup.setOnCheckedStateChangeListener { _, checkedId ->
            val filteredBooks = if (checkedId.isNotEmpty()) {
                when (checkedId.first()) {
                    R.id.chip_favorites -> favouriteBooks
                    R.id.chip_finished -> finishedBooks
                    R.id.chip_want_to_read -> wantToReadBooks
                    else -> books
                }
            } else {
                books
            }

            adapter.setBooks(filteredBooks)
        }
    }

    fun fetchAllBooks() {
        lifecycleScope.launch {
            viewModel.getAllBooks().collectLatest { books ->
                this@LibraryFragment.books = books   // store full list

                var favouriteCount = 0
                var finishedCount = 0
                var wantToReadCount = 0


                for (book in books) {
                    if (book.isFavourite) {
                        favouriteBooks.add(book)
                        favouriteCount++
                    }

                    if (book.isFinished) {
                        finishedBooks.add(book)
                        finishedCount++
                    }

                    if (book.wantToRead) {
                        wantToReadBooks.add(book)
                        wantToReadCount++
                    }
                }

                val checkedId = binding.categoryChipGroup.checkedChipId

                val filteredBooks = when (checkedId) {
                    R.id.chip_favorites -> favouriteBooks
                    R.id.chip_finished -> finishedBooks
                    R.id.chip_want_to_read -> wantToReadBooks
                    else -> books
                }

                binding.chipAll.text = "All (${books.size})"
                binding.chipFavorites.text = "Favourite ($favouriteCount)"
                binding.chipFinished.text = "Finished ($finishedCount)"
                binding.chipWantToRead.text = "Want to Read ($wantToReadCount)"

                adapter.setBooks(filteredBooks)
            }
        }
    }


    override fun onBookOpened(book: Book) {

        val bookCopy = book.copy(lastDateOpened = LocalDateTime.now().toString())
        viewModel.updateBook(bookCopy)

        val intent = Intent(requireContext(), ReaderActivity::class.java)
        intent.putExtra(BOOK_ID, book.id)
        startActivity(intent)
    }

    override fun onThreeDotsClicked(view: View, book: Book) {

        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.menu_library_book, popup.menu)

        if (book.isFinished)
            popup.menu.findItem(R.id.mark_as_finished).title = "Mark as unfinished"

        if (book.isFavourite)
            popup.menu.findItem(R.id.add_to_favourites).title = "Remove from favourites"

        if (book.wantToRead)
            popup.menu.findItem(R.id.want_to_read).title = "Remove from want to read"

        popup.show()

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.mark_as_finished -> markAsFinished(book)
                R.id.delete -> deleteBook(book)
                R.id.add_to_favourites -> addToFavourites(book)
                R.id.want_to_read -> addToWantToRead(book)
                R.id.details -> showDetails(book)
            }

            true

        }
    }

    private fun showDetails(book: Book) {

        val fragment = BookDetailsBottomSheetDialogFragment().apply {
            arguments = bundleOf("book" to book)
        }
        fragment.show(parentFragmentManager, "BookDetailsBottomSheetDialogFragment")

    }

    private fun addToWantToRead(book: Book) {

        if (book.wantToRead) {
            val bookCopy = book.copy(wantToRead = false)
            viewModel.updateBook(bookCopy)
            Toast.makeText(
                requireContext(),
                "Book removed from want to read successfully",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val bookCopy = book.copy(wantToRead = true)
            viewModel.updateBook(bookCopy)
            Toast.makeText(
                requireContext(),
                "Book successfully added to want to read",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deleteBook(book: Book) {
        showAlertDeleteDialog(book)
    }

    fun addToFavourites(book: Book) {

        if (book.isFavourite) {
            val bookCopy = book.copy(isFavourite = false)
            viewModel.updateBook(bookCopy)
            Toast.makeText(
                requireContext(),
                "Book removed from favourites successfully",
                Toast.LENGTH_SHORT
            ).show()
        } else {

            val bookCopy = book.copy(isFavourite = true)
            viewModel.updateBook(bookCopy)
            Toast.makeText(
                requireContext(),
                "Book successfully added to favourites",
                Toast.LENGTH_SHORT
            ).show()
        }

    }


    fun markAsFinished(book: Book) {

        if (book.isFinished) {
            val bookCopy = book.copy(isFinished = false)
            viewModel.updateBook(bookCopy)
            Toast.makeText(
                requireContext(),
                "Book unmarked as finished successfully",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val bookCopy = book.copy(isFinished = true)
            viewModel.updateBook(bookCopy)
            Toast.makeText(
                requireContext(),
                "Book successfully marked as finished",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    fun showAlertDeleteDialog(book: Book) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Warning")
            .setMessage("Are you sure you want to delete this book?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteBook(book)
                Toast.makeText(requireContext(), "Book deleted successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    companion object {
        const val BOOK_ID = "BOOK_ID"
    }


}
