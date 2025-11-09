package com.tohir.booksplusplus.ui.books

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.R

class DictionaryBottomSheet : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(word: String, pronunciation: String = "", definition: String = "", usages: String = ""): DictionaryBottomSheet  {

            val sheet = DictionaryBottomSheet()
            val args = Bundle().apply {
                putString("word", word)
                putString("pronunciation", pronunciation)
                putString("definition", definition)
                putString("usages", usages)
            }

            sheet.arguments = args

            return sheet
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dictionary_bottom_sheet, container, false)

        val wordText: TextView = view.findViewById(R.id.text_view_word_text)
        val definitionText: TextView = view.findViewById(R.id.text_view_definition_text)

        val word = arguments?.getString("word")
        val definition = arguments?.getString("definition")

        wordText.text = word
        definitionText.text = definition


        return view



    }



}