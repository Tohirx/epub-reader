package com.tohir.booksplusplus.ui.books.reader.dictionary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.data.database.dictionary.DictionaryModels
import com.tohir.booksplusplus.databinding.ItemWordBinding

class WordAdapter: RecyclerView.Adapter<WordAdapter.ViewHolder>() {

    private var definitions: List<DictionaryModels.Definition> = emptyList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder((ItemWordBinding.inflate(inflater, parent, false)))

    }

    fun setWords(definitions: List<DictionaryModels.Definition>) {
        this.definitions = definitions
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(definitions[position])
    }

    override fun getItemCount(): Int {
        return definitions.size
    }

    class ViewHolder(private val binding: ItemWordBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(definition: DictionaryModels.Definition) {

            binding.textViewDefinition.text = definition.definition
            if (!definition.example.isNullOrBlank()) {
                binding.textViewExample.text = definition.example
                binding.textViewExample.visibility = View.VISIBLE
            } else
                binding.textViewExample.visibility = View.GONE

        }
    }




}