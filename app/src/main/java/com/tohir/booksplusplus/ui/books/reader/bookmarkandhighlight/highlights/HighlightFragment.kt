package com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.highlights


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.databinding.FragmentHighlightsBinding
import com.tohir.booksplusplus.ui.books.reader.ReaderViewModel
import com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.BookmarkAndHighlightFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HighlightFragment() : Fragment(), HighlightClickedListener {

    private lateinit var binding: FragmentHighlightsBinding
    private val adapter = HighlightsAdapter(this)
    private val viewModel: HighlightViewModel by viewModels()
    private val readerViewModel: ReaderViewModel by activityViewModels()


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
            viewModel.getAllHighlights(bookId).collectLatest { highlights ->
                viewModel.getAllNotes(bookId).collectLatest { notes ->

                    val noteHighlights = notes.map { note ->
                        Highlight(bookId, Highlight.Style.HIGHLIGHT, "#FFD700".toColorInt(), note.locator, annotation = "", creation = note.date)
                    }
                    val combined = highlights + noteHighlights
                    adapter.setHighlights(combined)
                }
            }
        }
    }

    override fun onHighlightClicked(highlight: Highlight) {
        readerViewModel.setHighlight(highlight)
        (requireParentFragment() as? BookmarkAndHighlightFragment)?.dismiss()
    }

    override fun onHighlightLongClicked(highlight: Highlight): Boolean {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Highlight about to be deleted")
            .setMessage("Are you sure you want to delete this highlight")
            .setPositiveButton("Yes"
            ) { _, _ -> lifecycleScope.launch { viewModel.deleteHighlight(highlight.id) }  }
            .setNegativeButton("No") {dialog, _ -> dialog.dismiss()}
            .show()

        return true

    }

}