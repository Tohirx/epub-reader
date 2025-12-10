package com.tohir.booksplusplus.ui.books.reader.dictionary

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.data.database.DictionaryProvider
import com.tohir.booksplusplus.databinding.DictionaryBottomSheetBinding
import com.tohir.booksplusplus.dictionary.DictionaryApi
import com.tohir.booksplusplus.ui.books.reader.WordAdapter
import kotlinx.coroutines.launch

class DictionaryBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DictionaryBottomSheetBinding

    companion object {
        fun newInstance(word: String): DictionaryBottomSheet {

            val sheet = DictionaryBottomSheet()
            val args = Bundle().apply{ putString("word", word) }

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
    ): View {

        binding = DictionaryBottomSheetBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showLoadingState()

        val selectedWord = arguments?.getString("word")

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = fetchDictionaryData(selectedWord = selectedWord!!)
                showSuccessState(result)
            } catch (e: Exception) {
                showErrorState(e)
            }
        }
    }

    private fun showErrorState(e: Exception) {
        e.message?.let { Log.d("tohir", it) }
    }

    private fun showSuccessState(data: DictionaryResult) {
        binding.loadingSpinner.visibility = View.GONE

        binding.resultContentGroup.visibility = View.VISIBLE


        if (data.definitions.isEmpty()) {
            binding.textViewWordText.text = "No available definitions"
            binding.buttonPlayPronunciation.visibility = View.INVISIBLE
            binding.textViewExamplesTitle.visibility = View.INVISIBLE
            binding.textViewExamplesTitle.visibility = View.INVISIBLE
        }  else {

            binding.textViewWordText.text = data.selectedWord

            binding.recyclerViewDefinition.apply {
                adapter = WordAdapter().apply {
                    setWords(data.definitions)
                }
            }


            if (data.usages.isNotEmpty()) {
                binding.recyclerViewExamples.apply {
                    val examplesAdapter = WordAdapter().apply {
                        setWords(data.usages)
                    }
                    adapter = examplesAdapter
                }
            } else {
                binding.textViewExamplesTitle.visibility = View.INVISIBLE
            }

            binding.textViewPosText.text = "(${data.partOfSpeech})"


            if (data.audioUrl != null) {

                binding.buttonPlayPronunciation.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {

                        data.audioUrl.let { url ->
                            val mediaPlayer = MediaPlayer()
                            mediaPlayer.setDataSource(url)
                            mediaPlayer.prepareAsync()
                            mediaPlayer.setOnPreparedListener { mp -> mp.start() }
                        }

                    }
                }

            } else {
                binding.buttonPlayPronunciation.visibility = View.INVISIBLE
            }
        }
    }

    private fun showLoadingState() {
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.resultContentGroup.visibility = View.GONE
    }

    private suspend fun fetchDictionaryData(selectedWord: String): DictionaryResult {
        val api = DictionaryApi()
        val result = api.lookup(selectedWord)

        if (result.isSuccess) {

            val entries = result.getOrNull()!!
            val entry = entries[0]


            val definitions = arrayListOf<String>()
            val pos = arrayListOf<String>()
            val usages = arrayListOf<String>()

            var audioUrl: String? = null
            entries.forEach { entry ->
                entry.phonetics.forEach { phonetic ->
                    if (!phonetic.audio.isNullOrEmpty()) {
                        audioUrl = phonetic.audio
                        return@forEach
                    }
                }
            }


            entry.meanings.forEach { meaning ->

                pos.add(meaning.partOfSpeech)

                meaning.definitions.forEach { def ->

                    definitions.add(def.definition)

                    def.example?.let { usages.add(it) }
                }
            }

            return DictionaryResult(selectedWord, definitions, pos[0], usages, audioUrl)

        }

        val db = DictionaryProvider.getInstance(requireContext())
        val definitions = db.dictionaryDao().getDefinitions(selectedWord)

        val pos = db.dictionaryDao().getPos(selectedWord)

        val usages = db.dictionaryDao().getUsageExamples(selectedWord)


        return DictionaryResult(selectedWord, definitions, pos, usages)



    }
}