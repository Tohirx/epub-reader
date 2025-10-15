package com.tohir.booksandstuff.ui.books

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
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
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import java.io.File

class EpubReaderFragment : Fragment() {

    private val viewModel: ReaderViewModel by viewModels()
    lateinit var navigator: EpubNavigatorFragment
    private lateinit var binding: FragmentReaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)


        val bookPath = arguments?.getString("BOOK_PATH")
        val bookUri = arguments?.getString("BOOK_URI")
        val bookID = arguments?.getInt("BOOK_ID")
        Log.d("tohir", "EpubReaderFragment, Gotten $bookUri")

        if (bookPath != null) {
            lifecycleScope.launch {
                val publication =
                    viewModel.importPublication(bookPath.toUri(), requireContext(), bookID)

                val navigatorFactory = EpubNavigatorFactory(publication = publication)

                childFragmentManager.fragmentFactory =
                    navigatorFactory.createFragmentFactory(
                        viewModel.restoreReadingProgression(bookID!!)
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

                navigator = childFragmentManager.findFragmentByTag(tag) as EpubNavigatorFragment

                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    navigator.currentLocator
                        .onEach { viewModel.saveReadingProgression(it, bookID) }
                        .launchIn(this)
                }

            }
        } else {
            lifecycleScope.launch {
                val publication =
                    viewModel.importPublication(File(bookUri!!).toUri(), requireContext(), bookID)

                val navigatorFactory = EpubNavigatorFactory(publication = publication)

                childFragmentManager.fragmentFactory =
                    navigatorFactory.createFragmentFactory(
                        viewModel.restoreReadingProgression(
                            bookID!!
                        )
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

                navigator = childFragmentManager.findFragmentByTag(tag) as EpubNavigatorFragment

                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    navigator.currentLocator
                        .onEach { viewModel.saveReadingProgression(it, bookID) }
                        .launchIn(this)
                }

            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentReaderBinding.inflate(inflater, container, false)
        return binding.root
    }


}
