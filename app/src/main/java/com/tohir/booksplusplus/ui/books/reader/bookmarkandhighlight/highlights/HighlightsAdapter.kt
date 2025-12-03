package com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.highlights

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.data.model.Highlight
import com.tohir.booksplusplus.databinding.ItemHighlightsBinding

class HighlightsAdapter(private val listener: HighlightClickedListener) : RecyclerView.Adapter<HighlightsAdapter.ViewHolder>() {

     private var highlights = listOf<Highlight>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemHighlightsBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(highlights[position])
    }

    override fun getItemCount(): Int {
        return highlights.size
    }

     fun setHighlights(highlights: List<Highlight>) {
         this.highlights = highlights
         notifyDataSetChanged()
     }


     inner class ViewHolder(private val binding: ItemHighlightsBinding) : RecyclerView.ViewHolder(binding.root) {

         fun bind(highlight: Highlight) {

             val highlightedText = highlight.text.highlight
             val color = highlight.tint


             val fullText = "${highlight.text.before} $highlightedText".trim()

             val spannable = SpannableString(fullText)

             val startIndex = fullText.indexOf(highlightedText!!)
             val endIndex = startIndex + highlightedText.length

             spannable.setSpan(
                 BackgroundColorSpan(color),
                 startIndex,
                 endIndex,
                 Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
             )

             binding.textViewHighlight.text = spannable
             binding.textViewPageNumber.text = highlight.locations.position.toString()
             binding.textViewDate.text = highlight.creation

             binding.root.setOnClickListener {

                 listener.onHighlightClicked(highlight)

             }

             binding.root.setOnLongClickListener {
                 listener.onHighlightLongClicked(highlight)
             }
         }

    }

}

interface HighlightClickedListener {
    fun onHighlightClicked(highlight: Highlight)
    fun onHighlightLongClicked(highlight: Highlight): Boolean

}