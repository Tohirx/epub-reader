package com.tohir.booksandstuff.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tohir.booksandstuff.R
import com.tohir.booksandstuff.data.model.Book
import com.tohir.booksandstuff.databinding.ItemBookBinding
import java.io.File

class BookAdapter(private val listener: BookClickListener) : RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    private var books: List<Book> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemBookBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int {
        return books.size
    }

    fun setBooks(books: List<Book>) {
        this.books = books
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {

            val file = File(book.cover!!)
            val uri = Uri.fromFile(file)

            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.gradient_background)
                .into(binding.imageViewBookCover)

            binding.root.setOnLongClickListener {
                listener.onBookDeleted(book)
                true
            }

            binding.root.setOnClickListener {
                listener.onBookOpened(book)
            }
        }

    }

    interface BookClickListener {
        fun onBookDeleted(book: Book)
        fun onBookOpened(book: Book)

    }
}