package com.tohir.booksplusplus.ui.books

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.databinding.ItemSearchResultBinding
import org.readium.r2.shared.publication.Locator

class SearchResultAdapter : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    private var locators: List<Locator> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemSearchResultBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(locators[position])
    }

    fun setLocators(locators: List<Locator>) {
        this.locators = locators
        notifyDataSetChanged()
    }

    override fun getItemCount() = locators.size

    inner class ViewHolder(private val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(locator: Locator) {


            val fullText = locator.text.before + locator.text.highlight + locator.text.after

            val highlightedText = locator.text.highlight


            val spannable = SpannableString(fullText)

            val startIndex = fullText.indexOf(highlightedText!!)
            val endIndex = startIndex + highlightedText.length

            spannable.setSpan(
                BackgroundColorSpan("#FFEB3B".toColorInt()),
                startIndex,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            binding.textViewSearchResult.text = spannable


        }
    }
}