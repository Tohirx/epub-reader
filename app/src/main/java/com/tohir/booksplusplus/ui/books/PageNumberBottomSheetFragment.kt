package com.tohir.booksplusplus.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.databinding.PageNumberInputLayoutBinding

class PageNumberBottomSheetFragment : BottomSheetDialogFragment() {

    private val readerViewModel: ReaderViewModel by activityViewModels()
    private lateinit var binding: PageNumberInputLayoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = PageNumberInputLayoutBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupClickListeners()


    }

    private fun setupClickListeners() {

        binding.buttonGo.setOnClickListener {
            readerViewModel.setPage(binding.editTextPageNumber.text.toString().trim().toInt())
            dialog?.dismiss()
        }

        binding.editTextPageNumber.addTextChangedListener { text ->
            binding.buttonGo.isEnabled = !text.isNullOrEmpty()

        }
    }
}