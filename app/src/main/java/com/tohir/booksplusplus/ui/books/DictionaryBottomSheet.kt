package com.tohir.booksplusplus.ui.books

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.R

class DictionaryBottomSheet : BottomSheetDialogFragment() {


    companion object {
        fun newInstance(
            word: String,
            definition: List<String>,
            pos: String?,
            usages: List<String>
        ): DictionaryBottomSheet {

            val sheet = DictionaryBottomSheet()
            val args = Bundle().apply {
                putString("word", word)
                putString("pos", pos)
                putStringArrayList("definition", ArrayList(definition))
                putStringArrayList("usages", ArrayList(usages))
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

        view.findViewById<TextView>(R.id.text_view_word_text).text = arguments?.getString("word")

        view.findViewById<RecyclerView>(R.id.recycler_view_definition).apply {
            adapter = WordAdapter().apply {
                setWords(
                    arguments?.getStringArrayList("definition")
                        ?: listOf("No available definitions")
                )
            }
        }


        view.findViewById<RecyclerView>(R.id.recycler_view_examples).apply {
            val examplesAdapter = WordAdapter().apply {
                setWords(
                    arguments?.getStringArrayList("usages") ?: listOf("")
                )
            }
            adapter = examplesAdapter
        }


        val pos = arguments?.getString("pos")


        if ( pos != null)
            view.findViewById<TextView>(R.id.text_view_pos_text).text = "($pos)"


        return view

    }

}