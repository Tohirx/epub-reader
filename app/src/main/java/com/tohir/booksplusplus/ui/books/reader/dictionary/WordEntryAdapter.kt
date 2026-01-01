package com.tohir.booksplusplus.ui.books.reader.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.data.database.dictionary.DictionaryModels
import com.tohir.booksplusplus.databinding.ItemWordEntryBinding

class WordEntryAdapter : RecyclerView.Adapter<WordEntryAdapter.ViewHolder>() {
    private var wordEntries: List<DictionaryModels.WordEntry> = listOf()
    private lateinit var word: String

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder((ItemWordEntryBinding.inflate(inflater, parent, false)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(wordEntries[position])
    }

    fun setWordEntries(entries: List<DictionaryModels.WordEntry>) {
        this.wordEntries = entries
    }

    fun setWord(word: String) {
        this.word = word
    }

    override fun getItemCount(): Int {
        return wordEntries.size
    }

    inner class ViewHolder(private val binding: ItemWordEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(wordEntry: DictionaryModels.WordEntry) {

            val meaningsAdapter = MeaningsAdapter()
            meaningsAdapter.setMeanings(wordEntry.meanings)
            meaningsAdapter.setWord(word)

            binding.recyclerViewMeanings.adapter = meaningsAdapter

        }
    }

}