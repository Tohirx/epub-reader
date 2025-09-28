package com.tohir.booksandstuff.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.databinding.ItemBookBinding

class BookAdapter : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    private var books: List<Book> = listOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemBookBinding.inflate(inflater, parent, false))

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

    inner class ViewHolder(binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {

        }

    }
}