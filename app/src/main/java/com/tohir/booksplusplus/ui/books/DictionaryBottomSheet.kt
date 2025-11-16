package com.tohir.booksplusplus.ui.books

import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.databinding.DictionaryBottomSheetBinding

class DictionaryBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DictionaryBottomSheetBinding

    companion object {
        fun newInstance(
            word: String,
            definition: List<String>,
            pos: String?,
            usages: List<String>,
            audioUrl: String?
        ): DictionaryBottomSheet {

            val sheet = DictionaryBottomSheet()
            val args = Bundle().apply {
                putString("word", word)
                putString("pos", pos)
                putStringArrayList("definition", ArrayList(definition))
                putStringArrayList("usages", ArrayList(usages))
                putString("audio", audioUrl)
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
    ): View {

        binding = DictionaryBottomSheetBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val word = arguments?.getString("word")
        val definitions = arguments?.getStringArrayList("definition")

        if (definitions!!.isEmpty()) {
            binding.textViewWordText.text = "No available definitions"
            binding.buttonPlayPronunciation.visibility = View.INVISIBLE
            binding.textViewExamplesTitle.visibility = View.INVISIBLE
            binding.textViewExamplesTitle.visibility = View.INVISIBLE

        } else {

            binding.textViewWordText.text = word

            binding.recyclerViewDefinition.apply {
                adapter = WordAdapter().apply {
                    setWords(definitions)
                }
            }

            val examples = arguments?.getStringArrayList("usages")

            if (examples != null && !examples.isEmpty()) {
                binding.recyclerViewExamples.apply {
                    val examplesAdapter = WordAdapter().apply {
                        setWords(arguments?.getStringArrayList("usages") ?: listOf(""))
                    }
                    adapter = examplesAdapter
                }
            } else {
                binding.textViewExamplesTitle.visibility = View.INVISIBLE
            }


            val pos = arguments?.getString("pos")

            if (pos != null)
                binding.textViewPosText.text = "($pos)"
            else
                binding.textViewPosText.visibility = View.INVISIBLE



            val audioUrl = arguments?.getString("audio")

            if (audioUrl != null) {

                binding.buttonPlayPronunciation.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {

                        audioUrl.let { url ->
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
}