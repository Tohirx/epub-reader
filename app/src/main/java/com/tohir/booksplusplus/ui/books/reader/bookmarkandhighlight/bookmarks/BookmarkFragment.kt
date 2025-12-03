package com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.bookmarks

import android.R.attr.dial
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.databinding.FragmentBookmarkBinding
import com.tohir.booksplusplus.ui.books.reader.ReaderViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment(), OnBookmarkClickListener {

    private val adapter = BookmarkAdapter(this)
    private val readerViewModel: ReaderViewModel by activityViewModels()
    private val viewModel: BookmarkViewModel by viewModels()
    private lateinit var binding: FragmentBookmarkBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val divider = MaterialDividerItemDecoration(binding.recyclerViewBookmark.context,
            LinearLayoutManager.VERTICAL)

        divider.dividerThickness = resources.getDimensionPixelSize(R.dimen.divider_thickness)
        divider.dividerColor = MaterialColors.getColor(binding.recyclerViewBookmark, com.google.android.material.R.attr.colorOutline)

        binding.recyclerViewBookmark.addItemDecoration(divider)

        binding.recyclerViewBookmark.adapter = adapter
        binding.recyclerViewBookmark.layoutManager = LinearLayoutManager(requireContext())

        val bookId = arguments?.getLong("bookId")

        setBookmarks(bookId!!)

    }

    private fun setBookmarks(bookId: Long) {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllBookmarks(bookId).collectLatest { bookmarks ->
                adapter.setBookmarks(bookmarks)
            }

        }

    }

    override fun onBookmarkClicked(bookmark: Bookmark) {

        readerViewModel.setBookmark(bookmark)
    }

    override fun onBookmarkLongPressed(bookmark: Bookmark): Boolean {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Bookmark about to be deleted")
            .setMessage("Are you sure you want to delete this bookmark")
            .setPositiveButton("Yes"
            ) { _, _ -> lifecycleScope.launch { viewModel.deleteBookMark(bookmark.id) }  }
            .setNegativeButton("No") {dialog, _ -> dialog.dismiss()}
            .show()

        return true
    }


}