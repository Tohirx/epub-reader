package com.tohir.booksandstuff.ui.books





import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeFragment : Fragment(), RecentBookAdapter.OnRecentBooksClickedListener {

    private val viewModel: HomeViewModel by viewModels()
    private val adapter = RecentBookAdapter(this)
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.recyclerViewPreviouslyRead.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerViewPreviouslyRead.adapter = adapter

        fetchAllBooks()

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
}