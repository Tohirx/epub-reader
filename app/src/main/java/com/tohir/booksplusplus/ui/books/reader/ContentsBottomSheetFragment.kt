package com.tohir.booksplusplus.ui.books.reader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.databinding.FragmentContentsBottomSheetBinding
import org.readium.r2.shared.publication.Link

class ContentsBottomSheetFragment : BottomSheetDialogFragment(),
    ContentsAdapter.OnTableOfContentsClickListener {

    private lateinit var binding: FragmentContentsBottomSheetBinding
    private val adapter = ContentsAdapter(this)
    private val readerViewModel: ReaderViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentsBottomSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        readerViewModel.publication.observe(viewLifecycleOwner) { publication ->

            val links = publication.tableOfContents

            if (!links.isEmpty())
                adapter.setToc(links)
            else
                binding.textViewNoContents.visibility = View.VISIBLE

        }

        binding.recyclerViewContents.adapter = adapter

    }

    override fun onTableOfContentClicked(link: Link) {
        readerViewModel.setLink(link)
        dialog?.dismiss()
    }
}