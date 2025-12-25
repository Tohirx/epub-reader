package com.tohir.booksplusplus.ui.books

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.databinding.ItemBookBinding
import java.io.File
import kotlin.math.ceil

class BookAdapter(private val listener: BookClickListener) :
    RecyclerView.Adapter<BookAdapter.ViewHolder>() {

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

    inner class ViewHolder(private val binding: ItemBookBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {

            val file = File(book.cover!!)
            val uri = Uri.fromFile(file)

            Picasso.get()
                .load(uri)
                .into(binding.imageViewBookCover)

            binding.imageButtonOptions.setOnClickListener {
                listener.onThreeDotsClicked(it, book)

            }

            if (book.isFinished) binding.readingPercent.text = "Finished"
            else if (book.readingProgressDouble != null) {
                if (book.readingProgressDouble * 100 < 1) {
                    binding.readingPercent.text = "1%"
                } else {
                    val value = ceil(book.readingProgressDouble * 100)
                    binding.readingPercent.text = value.toInt().toString() + "%"
                }
            }

            if (book.lastDateOpened == null) binding.readingPercent.text = "New"

            binding.root.setOnClickListener {
                listener.onBookOpened(book)
            }
        }

    }

    interface BookClickListener {
        fun onBookOpened(book: Book)

        fun onThreeDotsClicked(view: View, book: Book)

    }
}