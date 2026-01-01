package com.tohir.booksplusplus.ui.books.reader.dictionary

import android.view.LayoutInflater
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
        }

    }
}