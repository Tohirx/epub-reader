package com.tohir.booksplusplus.ui.books.reader.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.databinding.FragmentSearchServiceBottomSheetBinding
import com.tohir.booksplusplus.ui.books.reader.ReaderViewModel
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.services.search.SearchIterator
import org.readium.r2.shared.publication.services.search.search

class SearchServiceFragmentBottomSheet : BottomSheetDialogFragment(), OnSearchResultClickListener {
    private lateinit var binding: FragmentSearchServiceBottomSheetBinding
    private var iterator: SearchIterator? = null
    private val readerViewModel: ReaderViewModel by activityViewModels()

    private val adapter = SearchResultAdapter(this)

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


        setupClickListeners()

        binding.recyclerViewSearchResult.adapter = adapter

    }

    private fun setupClickListeners() {
        binding.editTextSearch.setOnEditorActionListener { _, actionId, event ->
            // Check if the action is the "Done" key (IME_ACTION_DONE)
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                val text = binding.editTextSearch.text.toString()

                readerViewModel.publication.observe(viewLifecycleOwner) { publication ->
                    if (publication != null) {
                        performSearch(publication, text)
                        hideKeyboard()
                    }
                }
                true
            } else {
                false
            }
        }


        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearchResult.layoutManager = layoutManager

        binding.recyclerViewSearchResult.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisible = layoutManager.findLastVisibleItemPosition()
                val totalItems = layoutManager.itemCount

                if (lastVisible == totalItems - 1) {

                    viewLifecycleOwner.lifecycleScope.launch {

                        val result = iterator?.next()?.getOrNull()

                        val locators = ArrayList( result?.locators.orEmpty())
                        adapter.addLocators(locators)

                    }

                }
            }
        })
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun performSearch(publication: Publication, text: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            iterator = publication.search(text)
            val result = iterator?.next()?.getOrNull()

            val locators = ArrayList(result?.locators.orEmpty())

            adapter.setLocators(locators)
        }
    }

    override fun onSearchResultClicked(locator: Locator) {

        readerViewModel.setLocator(locator)
        dialog?.dismiss()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        iterator?.close()
    }

}