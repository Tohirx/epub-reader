package com.tohir.booksplusplus.ui.books

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.databinding.FragmentHomeBinding
import com.tohir.booksplusplus.ui.books.reader.ReaderActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeFragment : Fragment(), RecentBookAdapter.OnRecentBooksClickedListener {

    private val viewModel: HomeViewModel by viewModels()
    private val adapter = RecentBookAdapter(this)
    private lateinit var binding: FragmentHomeBinding
    private val prefs by lazy {
        requireContext().getSharedPreferences(
            "user_pref",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.recyclerViewPreviouslyRead.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)

        binding.textViewMinutesReadValue.text = prefs.getInt("MINUTES", 0).toString()

        binding.recyclerViewPreviouslyRead.adapter = adapter

        fetchAllBooks()

    }

    override fun onResume() {
        super.onResume()

        binding.textViewMinutesReadValue.text = prefs.getInt("MINUTES", 0).toString()
    }

    fun fetchAllBooks() {
        lifecycleScope.launch {
            viewModel.getRecentBooks().collectLatest { books ->
                adapter.setBooks(books)
            }
        }

    }


    override fun onRecentBookClicked(book: Book) {

        val bookCopy = book.copy(lastDateOpened = LocalDateTime.now().toString())
        viewModel.updateBook(bookCopy)

        val intent = Intent(requireContext(), ReaderActivity::class.java)
        intent.putExtra("BOOK_URI", book.uri)
        intent.putExtra("BOOK_ID", book.id)
        startActivity(intent)
    }

    override fun onMarkAsCompleted(book: Book) {
        val bookCopy = book.copy(isComplete = true)
        viewModel.updateBook(bookCopy)
    }
}