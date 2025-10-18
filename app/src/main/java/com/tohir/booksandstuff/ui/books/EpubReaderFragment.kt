package com.tohir.booksandstuff.ui.books

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commitNow
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tohir.booksandstuff.R
import com.tohir.booksandstuff.databinding.FragmentReaderBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.readium.r2.navigator.VisualNavigator
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import java.io.File

class EpubReaderFragment : Fragment() {

    private val viewModel: ReaderViewModel by viewModels()
    private lateinit var binding: FragmentReaderBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookPath = arguments?.getString("BOOK_PATH")
        val bookUri = arguments?.getString("BOOK_URI")
        val bookID = arguments?.getInt("BOOK_ID")
        Log.d("tohir", "EpubReaderFragment, Gotten $bookUri")

        val bookFile = bookPath?.let { File(it) } ?: bookUri?.let { File(it) }

        if (bookFile != null && bookID != null) {
            lifecycleScope.launch {
                val publication =
                    viewModel.importPublication(bookFile.toUri(), requireContext(), bookID)

                val navigatorFactory = EpubNavigatorFactory(publication = publication)

                childFragmentManager.fragmentFactory =
                    navigatorFactory.createFragmentFactory(
                        viewModel.restoreReadingProgression(bookID)
                    )

                val tag = "EpubNavigatorFragment"
                if (savedInstanceState == null) {
                    childFragmentManager.commitNow {
                        add(
                            R.id.fragment_reader_container,
                            EpubNavigatorFragment::class.java,
                            Bundle(),
                            tag
                        )
                    }
                }

                setupClickListeners()

                val navigator = childFragmentManager.findFragmentByTag(tag) as EpubNavigatorFragment

                (navigator as VisualNavigator).apply {
                    addInputListener(object : InputListener {
                        override fun onTap(event: TapEvent): Boolean {

                            if (binding.imageViewCancelButton.isVisible) {
                                binding.imageViewCancelButton.visibility = View.INVISIBLE
                            } else if (binding.imageViewCancelButton.isInvisible) {
                                binding.imageViewCancelButton.visibility = View.VISIBLE
                            }

                            if (binding.imageViewOptions.isVisible) {
                                binding.imageViewOptions.visibility = View.INVISIBLE
                            } else if (binding.imageViewOptions.isInvisible) {
                                binding.imageViewOptions.visibility = View.VISIBLE
                            }



                            return true
                        }
                    })
                }

                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    navigator.currentLocator
                        .onEach { viewModel.saveReadingProgression(it, bookID) }
                        .launchIn(this)
                }
            }
        }
    }

    fun setupClickListeners() {
        binding.imageViewCancelButton.setOnClickListener {
            requireActivity().finish()
        }

        binding.imageViewOptions.setOnClickListener {

        }
    }

}
