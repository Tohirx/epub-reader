package com.tohir.booksplusplus.ui.books

import android.app.Dialog
import android.content.Context
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tohir.booksplusplus.databinding.DictionaryBottomSheetBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL

class DictionaryBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: DictionaryBottomSheetBinding

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
    ): View {

        binding = DictionaryBottomSheetBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val word = arguments?.getString("word")
        val definition = arguments?.getStringArrayList("definition")

        if (definition!!.isEmpty()) {
            binding.textViewWordText.text = "No available definitions"
            binding.buttonPlayPronunciation.visibility = View.INVISIBLE
            binding.textViewExamplesTitle.visibility = View.INVISIBLE

        } else {

            binding.textViewWordText.text = word

            binding.recyclerViewDefinition.apply {
                adapter = WordAdapter().apply {
                    setWords(
                        arguments?.getStringArrayList("definition")
                            ?: listOf("No available definitions")
                    )
                }
            }


            binding.recyclerViewExamples.apply {
                val examplesAdapter = WordAdapter().apply {
                    setWords(
                        arguments?.getStringArrayList("usages") ?: listOf("")
                    )
                }
                adapter = examplesAdapter
            }


            val pos = arguments?.getString("pos")


            if (pos != null)
                binding.textViewPosText.text = "($pos)"

            binding.buttonPlayPronunciation.apply {
                visibility = View.VISIBLE
                setOnClickListener {

                    viewLifecycleOwner.lifecycleScope.launch {

                        val cm =
                            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                        val network = cm.activeNetwork

                        if (network != null)
                            playPronunciation(word!!, "d90f7c8c-f886-4306-8ae2-8417947a653c")
                        else
                            Toast.makeText(requireContext(), "Network error", Toast.LENGTH_LONG)
                                .show()

                    }

                }
            }

        }


    }

    suspend fun playPronunciation(word: String, apiKey: String) {
        withContext(Dispatchers.IO) {
            val response =
                URL("https://dictionaryapi.com/api/v3/references/collegiate/json/$word?key=$apiKey").readText()
            val jsonArray = JSONArray(response)
            val first = jsonArray.getJSONObject(0)
            val soundObj = first.getJSONObject("hwi")
                .getJSONArray("prs")
                .getJSONObject(0)
                .getJSONObject("sound")

            val audioName = soundObj.getString("audio")
            val subfolder = audioName.first().toString()
            val audioUrl =
                "https://media.merriam-webster.com/audio/prons/en/us/mp3/$subfolder/$audioName.mp3"

            val player = MediaPlayer().apply {
                setDataSource(audioUrl)
                prepare()
                start()
            }
        }
    }
}