package com.tohir.booksplusplus.ui.books.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.databinding.FragmentOptionsBinding
import com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.BookmarkAndHighlightFragment
import com.tohir.booksplusplus.ui.books.reader.search.SearchServiceFragmentBottomSheet
import com.tohir.booksplusplus.ui.books.reader.toc.ContentsBottomSheetFragment


class OptionsFragment : Fragment() {

    private lateinit var binding: FragmentOptionsBinding
    private var bookId: Long? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentOptionsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookId = arguments?.getLong("bookId")

        setupClickListeners()

    }


    private fun setupClickListeners() {

        binding.touchInterceptor.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.cardViewThemesAndSettingsContainer.setOnClickListener {

           val fragment = ThemesAndSettingsFragment()
            fragment.show(parentFragmentManager, "ThemesAndSettingsFragment")
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()

        }

        binding.cardViewBookmarkAndHighlights.setOnClickListener {

            val fragment = BookmarkAndHighlightFragment().apply {
                arguments = bundleOf("bookId" to bookId)
            }

            fragment.show(parentFragmentManager, "BookmarkAndHighlightFragment")
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()

        }

        binding.cardViewContents.setOnClickListener {
            ContentsBottomSheetFragment().show(parentFragmentManager, "ContentBottomSheetFragment")
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        binding.cardViewSearch.setOnClickListener {
            SearchServiceFragmentBottomSheet().show(parentFragmentManager, "SearchServiceBottomSheet")
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }



    }

}