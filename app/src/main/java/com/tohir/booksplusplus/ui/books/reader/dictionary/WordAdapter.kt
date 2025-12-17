package com.tohir.booksplusplus.ui.books.reader.dictionary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.databinding.ItemWordBinding

class WordAdapter: RecyclerView.Adapter<WordAdapter.ViewHolder>() {

    private var words: List<String> = emptyList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder((ItemWordBinding.inflate(inflater, parent, false)))

    }

    fun setWords(words: List<String>) {
        this.words = words
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.textView.text = words[position] + "."
    }

    override fun getItemCount(): Int {
        return words.size
    }

    class ViewHolder(binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root) {
        val textView = binding.textViewWord
    }




}