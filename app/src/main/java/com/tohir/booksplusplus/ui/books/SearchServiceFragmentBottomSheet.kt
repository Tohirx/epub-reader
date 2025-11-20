package com.tohir.booksplusplus.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.databinding.FragmentSearchServiceBottomSheetBinding

class SearchServiceFragmentBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentSearchServiceBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchServiceBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}
