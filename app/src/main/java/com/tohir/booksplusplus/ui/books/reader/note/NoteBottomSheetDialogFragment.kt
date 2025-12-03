package com.tohir.booksplusplus.ui.books.reader.note

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.databinding.BottomSheetDialogFragmentAddNoteBinding
import kotlinx.coroutines.launch
import org.readium.r2.shared.publication.Locator

class NoteBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: BottomSheetDialogFragmentAddNoteBinding
    private val viewModel: NoteViewModel by viewModels()
    private val noteId by lazy { arguments?.getLong("noteId", -1)!! }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDialogFragmentAddNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookId = arguments?.getLong("bookId")!!

        val locator: Locator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("locator", Locator::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("locator")
        }


        if (noteId != -1L) {
            viewLifecycleOwner.lifecycleScope.launch {
                val note = viewModel.findNoteById(noteId)
                binding.editTextNote.setText(note.content)
                binding.textViewTitle.text = "Edit Note"
                binding.textViewHighlightPreview.text = note.text.highlight
                binding.buttonDeleteNote.visibility = View.VISIBLE
            }

            setupClickListeners(bookId = bookId)


        }

        binding.buttonDeleteNote.setOnClickListener {
            viewModel.deleteNoteById(noteId)
            dialog?.dismiss()
        }

        binding.buttonCancelNote.setOnClickListener {
            dialog?.dismiss()
        }


        setupObservers()

        locator?.let {
            binding.textViewHighlightPreview.text = it.text.highlight

            setupClickListeners(it, bookId)
        }

    }

    fun setupClickListeners(locator: Locator? = null, bookId: Long) {
        binding.buttonSaveNote.setOnClickListener {
            if (noteId == -1L && locator != null) {
                val content = binding.editTextNote.text.toString()
                viewModel.addNote(locator, bookId, content)
            } else {
                lifecycleScope.launch {
                    val note = viewModel.findNoteById(noteId)
                    val noteCopy = note.copy(content = binding.editTextNote.text.toString())
                    viewModel.updateNote(noteCopy)

                }
            }
            dialog?.dismiss()
        }
    }

    fun setupObservers() {
        binding.editTextNote.addTextChangedListener { text ->
            binding.buttonSaveNote.isEnabled = !text.isNullOrEmpty()
        }
    }
}