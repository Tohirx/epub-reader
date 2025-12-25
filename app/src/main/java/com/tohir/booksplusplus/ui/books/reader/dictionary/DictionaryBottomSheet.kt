package com.tohir.booksplusplus.ui.books.reader.dictionary

import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.data.database.dictionary.DictionaryApi
import com.tohir.booksplusplus.data.database.dictionary.DictionaryModels
import com.tohir.booksplusplus.data.database.dictionary.DictionaryProvider
import com.tohir.booksplusplus.databinding.DictionaryBottomSheetBinding
import kotlinx.coroutines.launch

class DictionaryBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DictionaryBottomSheetBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            binding.buttonPlayPronunciation.visibility = View.GONE

        } else {

            binding.textViewWordText.text = data.selectedWord

            binding.recyclerViewDefinitions.apply {
                adapter = WordAdapter().apply {
                    setWords(data.definitions)
                }
            }


            binding.textViewPosText.text = "${data.partOfSpeech}"

            if (data.audioUrls.isNotEmpty()) {

                binding.buttonPlayPronunciation.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {

                        for (audio in data.audioUrls) {
                            val mediaPlayer = MediaPlayer()
                            mediaPlayer.setDataSource(audio)
                            mediaPlayer.prepareAsync()
                            mediaPlayer.setOnPreparedListener { mp -> mp.start() }

                            mediaPlayer.setOnCompletionListener {
                                mediaPlayer.release()
                            }
                            break
                        }
                    }
                }

            } else {
                binding.buttonPlayPronunciation.visibility = View.GONE
            }
        }
    }

    private fun showLoadingState() {
        binding.loadingSpinner.visibility = View.VISIBLE
        binding.resultContentGroup.visibility = View.GONE
    }

    private suspend fun fetchDictionaryData(selectedWord: String): DictionaryResult {

        val connectivityManager = requireContext().getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        if (connectivityManager.activeNetwork != null) {

            val api = DictionaryApi()

            val result = api.lookup(selectedWord.lowercase())

            if (result.isSuccess) {

                val entries = result.getOrNull()!!

                val definitions: ArrayList<DictionaryModels.Definition> = arrayListOf()
                val pos = arrayListOf<String>()
                val audioUrls = arrayListOf<String>()

                entries.forEach { entry ->
                    entry.phonetics.forEach { phonetic ->
                        if (!phonetic.audio.isNullOrBlank()) {
                            audioUrls.add(phonetic.audio)
                        }
                    }
                }

                entries.forEach { entry ->

                    entry.meanings.forEach { meaning ->

                        pos.add(meaning.partOfSpeech)

                        meaning.definitions.forEach { definition ->
                            definitions.add(definition)
                        }
                    }
                }

                return DictionaryResult(selectedWord, definitions, pos, audioUrls)

            }

        }

        val db = DictionaryProvider.getInstance(requireContext())

        val pos = db.dictionaryDao().getPos(selectedWord)
        val definitions = db.dictionaryDao().getDefinitions(selectedWord)

        val transformedDefinitions: List<DictionaryModels.Definition> = definitions.map {
            DictionaryModels.Definition(it)
        }

        return DictionaryResult(selectedWord, transformedDefinitions, pos)
    }
}