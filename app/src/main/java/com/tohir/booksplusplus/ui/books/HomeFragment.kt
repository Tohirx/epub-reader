package com.tohir.booksplusplus.ui.books

import android.content.Context
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.databinding.FragmentHomeBinding
import com.tohir.booksplusplus.ui.books.reader.ReaderActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeFragment : Fragment(), RecentBookAdapter.BookClickListener {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private val prefs by lazy {
        requireContext().getSharedPreferences(
            "user_pref",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()

        val minutesRead = prefs.getInt("MINUTES", 0)

        binding.textViewMinutesReadValue.text = minutesRead.toString()

        binding.textViewMinutes.text = if (minutesRead <= 1) "minute" else "minutes"

    }

    private fun setupAdapters() {

        // Finished books
        val finishedAdapter = RecentBookAdapter(this)
        binding.recyclerViewFinished.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = finishedAdapter
        }

        // Recently added
        val recentlyAddedAdapter = RecentBookAdapter(this)
        binding.recyclerViewRecentlyAdded.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = recentlyAddedAdapter
        }

        // Continue reading
        val continueReadingAdapter = RecentBookAdapter(this)
        binding.recyclerViewContinueReading.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = continueReadingAdapter
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.getFinishedBooks().collectLatest { books ->
                        val visible = books.isNotEmpty()
                        binding.textViewFinished.visibility =
                            if (visible) View.VISIBLE else View.GONE
                        binding.recyclerViewFinished.visibility =
                            if (visible) View.VISIBLE else View.GONE

                        finishedAdapter.setBooks(books)
                    }
                }

                launch {
                    viewModel.getRecentlyAddedBooks().collectLatest { books ->
                        val visible = books.isNotEmpty()
                        binding.textViewRecentlyAdded.visibility =
                            if (visible) View.VISIBLE else View.GONE
                        binding.recyclerViewRecentlyAdded.visibility =
                            if (visible) View.VISIBLE else View.GONE

                        recentlyAddedAdapter.setBooks(books)
                    }
                }

                launch {
                    viewModel.getRecentBooks().collectLatest { books ->
                        val visible = books.isNotEmpty()
                        binding.textViewContinueReading.visibility =
                            if (visible) View.VISIBLE else View.GONE
                        binding.recyclerViewContinueReading.visibility =
                            if (visible) View.VISIBLE else View.GONE

                        continueReadingAdapter.setBooks(books)
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        binding.textViewMinutesReadValue.text = prefs.getInt("MINUTES", 0).toString()
    }


    override fun onRecentBookClicked(book: Book) {

        val bookCopy = book.copy(lastDateOpened = LocalDateTime.now().toString())
        viewModel.updateBook(bookCopy)

        val intent = Intent(requireContext(), ReaderActivity::class.java)
        intent.putExtra("BOOK_ID", book.id)
        startActivity(intent)
    }

    override fun onMarkAsCompleted(book: Book) {
        val bookCopy = book.copy(isFinished = true)
        viewModel.updateBook(bookCopy)
    }

    override fun onThreeDotsClicked(
        view: View,
        book: Book
    ) {

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


}