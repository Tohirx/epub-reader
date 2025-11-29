package com.tohir.booksplusplus.ui.books.reader.bookmarkandhighlight.bookmarks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.data.model.Bookmark
import com.tohir.booksplusplus.databinding.ItemBookmarkBinding

class BookmarkAdapter(val listener: OnBookmarkClickListener) : RecyclerView.Adapter<BookmarkAdapter.ViewHolder>() {

    private var bookmarks: List<Bookmark> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemBookmarkBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bookmarks[position])
    }

    fun setBookmarks(bookmarks: List<Bookmark>) {
        this.bookmarks = bookmarks
        notifyDataSetChanged()
    }

    override fun getItemCount() = bookmarks.size

     inner class ViewHolder(private val binding: ItemBookmarkBinding): RecyclerView.ViewHolder(binding.root) {

         fun bind(bookmark: Bookmark) {

             binding.textViewBookmarkText.text = bookmark.title ?: bookmark.href
             binding.textViewPageNumber.text = bookmark.locations.position.toString()
             binding.textViewDateAdded.text = bookmark.creation

             binding.root.setOnClickListener {
                 listener.onBookmarkClicked(bookmark)
             }

         }

    }
}

interface OnBookmarkClickListener {
    fun onBookmarkClicked(bookmark: Bookmark)
}