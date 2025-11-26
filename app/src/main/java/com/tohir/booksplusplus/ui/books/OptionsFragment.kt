package com.tohir.booksplusplus.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.tohir.booksplusplus.databinding.FragmentOptionsBinding


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
        binding.imageButtonCancel.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .remove(this)
                .commit()
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