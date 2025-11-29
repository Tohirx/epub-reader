package com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.tohir.booksplusplus.databinding.FragmentBookmarkAndHighlightBinding
import com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.bookmarks.BookmarkFragment
import com.tohir.booksplusplus.ui.books.reader.ReaderViewModel
import com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.highlights.HighlightFragment

class BookmarkAndHighlightFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBookmarkAndHighlightBinding
    private val readerViewModel: ReaderViewModel by activityViewModels()
    private var bookId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookmarkAndHighlightBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        bookId = arguments?.getLong("bookId")


        binding.pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->

            when (position) {
                0 -> tab.text = "Highlights"
                else -> tab.text = "Bookmarks"
            }

        }.attach()

    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val bottomSheet = it.findViewById<View>(
                R.id.design_bottom_sheet
            )
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        }
    }



    inner class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {

            val highlightFragment = HighlightFragment().apply {
                arguments = bundleOf("bookId" to bookId)
            }

            val bookmarkFragment = BookmarkFragment().apply {
                arguments = bundleOf("bookId" to bookId)
            }

            return when (position) {
                0 -> highlightFragment
                else -> bookmarkFragment
            }
        }

        override fun getItemCount() = 2
    }
}