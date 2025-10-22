package com.tohir.booksandstuff.ui.books

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.databinding.FragmentLibraryBinding
import com.tohir.booksandstuff.ui.BookAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime


class LibraryFragment : Fragment(), BookAdapter.BookClickListener {

    private val viewModel: LibraryFragmentViewModel by viewModels()
    private val adapter = BookAdapter(this)
    private lateinit var binding: FragmentLibraryBinding


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
    }

    fun fetchAllBooks() {
        lifecycleScope.launch {
            viewModel.getAllBooks().collectLatest { books ->
                adapter.setBooks(books)
            }
        }
    }

    override fun onBookDeleted(book: Book) {
        showAlertDialog(book)
    }

    override fun onBookOpened(book: Book) {

        val bookCopy = book.copy(lastDateOpened = LocalDateTime.now().toString())
        viewModel.updateBook(bookCopy)

        val intent = Intent(requireContext(), ReaderActivity::class.java)
        intent.putExtra(BOOK_URI, book.uri)
        intent.putExtra(BOOK_ID, book.id)
        startActivity(intent)

    }

    fun showAlertDialog(book: Book) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Warning")
            .setMessage("Are you sure you want to delete this book?")
            .setPositiveButton("Yes") { _, _ -> viewModel.deleteBook(book) }
            .setNegativeButton(
                "No"
            ) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    companion object {
        const val BOOK_ID = "BOOK_ID"
        const val BOOK_URI = "BOOK_URI"
    }


}
