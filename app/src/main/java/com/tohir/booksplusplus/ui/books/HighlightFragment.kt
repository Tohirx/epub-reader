package com.tohir.booksplusplus.ui.books


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.databinding.FragmentHighlightsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HighlightFragment() : Fragment(), HighlightClickedListener {

    private lateinit var binding: FragmentHighlightsBinding
    private val adapter = HighlightsAdapter(this)
    private val viewModel: HighlightFragmentViewModel by viewModels()
    private val sharedViewModel: SharedHighlightViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHighlightsBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val divider = MaterialDividerItemDecoration(
            binding.recyclerViewHighlights.context,
            LinearLayoutManager.VERTICAL
        )

        divider.dividerThickness = resources.getDimensionPixelSize(R.dimen.divider_thickness)
        divider.dividerColor = MaterialColors.getColor(binding.recyclerViewHighlights, com.google.android.material.R.attr.colorOutline)

        binding.recyclerViewHighlights.addItemDecoration(divider)

        binding.recyclerViewHighlights.adapter = adapter
        binding.recyclerViewHighlights.layoutManager = LinearLayoutManager(requireContext())


        val bookId = arguments?.getLong("bookId")
        getHighlights(bookId!!)

    }

    fun getHighlights(bookId: Long) {
        lifecycleScope.launch {

            viewModel.getAllHighlights(bookId = bookId).collectLatest { highlights ->
                adapter.setHighlights(highlights)

                Log.d("tohir", highlights.size.toString())
            }

        }
    }

    override fun onHighlightClicked(highlight: Highlight) {
        sharedViewModel.setHighlight(highlight)
    }

}