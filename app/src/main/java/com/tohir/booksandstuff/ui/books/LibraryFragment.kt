package com.tohir.booksandstuff.ui.books

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.tohir.booksandstuff.ReaderActivity
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.databinding.FragmentLibraryBinding
import com.tohir.booksandstuff.ui.BookAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.services.cover
import java.io.File
import java.io.FileOutputStream


class LibraryFragment : Fragment() {

    private val viewModel: LibraryFragmentViewModel by viewModels()
    private val adapter = BookAdapter()
    private lateinit var binding: FragmentLibraryBinding


    //Callback for the result gotten from the file picker
    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

        if (uri != null) {
            viewModel.importPublication(uri, requireContext()) { publication ->

                lifecycleScope.launch {

                    val authors = publication.metadata.authors.joinToString(", ") { contributor ->
                        contributor.name
                    }

                    //This stores the cover in the App's directory as a PNG image
                    val file =
                        File(requireContext().filesDir, "cover_${publication.metadata.title}.png")
                    FileOutputStream(file).use { out ->
                        publication.cover()!!.compress(Bitmap.CompressFormat.PNG, 80, out)
                    }


                    viewModel.addBook(
                        Book(
                            title = publication.metadata.title,
                            author = authors,
                            cover = file.absolutePath
                        )
                    )

                }
                val intent = Intent(requireContext(), ReaderActivity::class.java)
                startActivity(intent)
            }

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.libraryRecyclerView.adapter = adapter
        fetchAllBooks()
        binding.libraryFragmentFab.setOnClickListener {
            getContent.launch("application/epub+zip")
        }


    }

    fun fetchAllBooks() {

        lifecycleScope.launch {
            viewModel.getAllBooks().collectLatest { books ->
                adapter.setBooks(books)
            }

        }


    }


}
