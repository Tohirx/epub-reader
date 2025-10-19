package com.tohir.booksandstuff.ui.books

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tohir.booksandstuff.R
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.databinding.ItemRecentlyReadBooksBinding
import java.io.File

class RecentBookAdapter : RecyclerView.Adapter<RecentBookAdapter.ViewHolder>() {

    private var books: List<Book> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemRecentlyReadBooksBinding.inflate(inflater, parent, false))

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
       holder.bind(books[position])
    }

    override fun getItemCount(): Int {

        return books.size
    }

    fun setBooks(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemRecentlyReadBooksBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book : Book) {
            val file = File(book.cover!!)
            val uri = Uri.fromFile(file)

            Picasso.get()
                .load(uri)
                .error(R.drawable.ic_launcher_background)
                .into(binding.imageViewRecentlyReadBookCover)
        }
    }
}