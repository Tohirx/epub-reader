package com.tohir.booksplusplus.ui.books.reader.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.data.database.dictionary.DictionaryModels
import com.tohir.booksplusplus.databinding.ItemMeaningsBinding

class MeaningsAdapter : RecyclerView.Adapter<MeaningsAdapter.ViewHolder>() {

    private var meanings: List<DictionaryModels.Meaning> = listOf()
    private lateinit var word: String

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemMeaningsBinding.inflate(layoutInflater, parent, false))
    }

    fun setMeanings(meanings: List<DictionaryModels.Meaning>) {
        this.meanings = meanings
    }

    fun setWord(word: String) {
        this.word = word
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(meanings[position], position)
    }

    override fun getItemCount(): Int {
        return meanings.size
    }



    inner class ViewHolder(private val binding: ItemMeaningsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(meaning: DictionaryModels.Meaning, position: Int) {
            binding.textViewPosText.text = "(${meaning.partOfSpeech})"

            val wordAdapter = WordAdapter()
            wordAdapter.setWords(meaning.definitions)
            binding.recyclerViewMeanings.adapter = wordAdapter
            binding.textViewWordCount.text = (position + 1).toString()
            binding.textViewWordText.text = word


            if (meaning.synonyms.isNotEmpty())
            {
                val synonyms = StringBuilder()
                for (s: String in meaning.synonyms) synonyms.append("$s, ")
                binding.textViewSynonyms.visibility = View.VISIBLE
                binding.textViewSynonyms.text = "Synonyms: ${synonyms.removeSuffix(", ")}"
            } else
                binding.textViewSynonyms.visibility = View.GONE


            if (meaning.antonyms.isNotEmpty())
            {
                val antonyms = StringBuilder()
                for (a: String in meaning.antonyms) antonyms.append("$a, ")
                binding.textViewAntonyms.visibility = View.VISIBLE
                binding.textViewAntonyms.text = "Antonyms: ${antonyms.removeSuffix(", ")}"
            } else
                binding.textViewAntonyms.visibility = View.GONE


        }

    }
}