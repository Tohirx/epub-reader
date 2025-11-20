package com.tohir.booksplusplus.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.databinding.FragmentOptionsBinding
import org.readium.r2.shared.publication.services.search.SearchService
import kotlin.getValue


class OptionsFragment : Fragment() {

    private lateinit var binding: FragmentOptionsBinding
    private val readerViewModel: ReaderViewModel by activityViewModels()
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

        readerViewModel.selectedHighlight.observe(viewLifecycleOwner) { _ ->
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
        }

        readerViewModel.link.observe(viewLifecycleOwner) { _ ->
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()

        }

    }

    private fun setupClickListeners() {
        binding.imageButtonCancel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commitNow()
        }

        binding.cardViewThemesAndSettingsContainer.setOnClickListener {

           val fragment = ThemesAndSettingsFragment()
            fragment.show(parentFragmentManager, "ThemesAndSettingsFragment")
        }

        binding.cardViewBookmarkAndHighlights.setOnClickListener {

            val fragment = BookmarkAndHighlightFragment().apply {
                arguments = bundleOf("bookId" to bookId)
            }

            fragment.show(parentFragmentManager, "BookmarkAndHighlightFragment")

        }

        binding.cardViewContents.setOnClickListener {
            ContentsBottomSheetFragment().show(parentFragmentManager, "ContentBottomSheetFragment")
        }

        binding.cardViewSearch.setOnClickListener {
            SearchServiceFragmentBottomSheet().show(parentFragmentManager, "SearchServiceBottomSheet")
        }



    }

}