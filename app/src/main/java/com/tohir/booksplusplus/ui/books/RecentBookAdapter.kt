package com.tohir.booksplusplus.ui.books

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tohir.booksplusplus.R
import com.tohir.booksplusplus.data.model.Book
import com.tohir.booksplusplus.databinding.ItemRecentlyReadBooksBinding
import java.io.File
import kotlin.math.ceil

class RecentBookAdapter(private val listener: BookClickListener) :
    RecyclerView.Adapter<RecentBookAdapter.ViewHolder>() {

    private var books: List<Book> = listOf()

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

    inner class ViewHolder(private val binding: ItemRecentlyReadBooksBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {

            val file = File(book.cover!!)
            val uri = Uri.fromFile(file)


            Picasso.get()
                .load(uri)
                .error(R.drawable.ic_launcher_background)
                .into(binding.imageViewRecentlyReadBookCover)

            binding.textViewAuthorName.text = book.author
            binding.textViewBookTitle.text = book.title

            if (book.readingProgressDouble != null) {
                if (book.readingProgressDouble * 100 < 1) {
                    binding.progressBarReading.setProgress(1, true)
                } else {
                    val value = ceil(book.readingProgressDouble * 100)
                    binding.progressBarReading.setProgress(value.toInt(), true)
                }
            }

            if (binding.progressBarReading.progress >= 95 && !book.isFinished) {
                binding.buttonMarkAsFinished.visibility = View.VISIBLE
                binding.buttonMarkAsFinished.setOnClickListener {
                    listener.onMarkAsCompleted(book)
                    binding.buttonMarkAsFinished.visibility = View.GONE
                }

            }

            if (book.isFinished) {
                binding.progressBarReading.visibility = View.GONE
                binding.textViewFinished.visibility = View.VISIBLE
            }

            binding.root.setOnClickListener {
                listener.onRecentBookClicked(book)
            }

            binding.imageButtonOptions.setOnClickListener {
                listener.onThreeDotsClicked(it, book)
            }

        }

    }

    interface BookClickListener {
        fun onRecentBookClicked(book: Book)
        fun onMarkAsCompleted(book: Book)
        fun onThreeDotsClicked(view: View, book: Book)
    }
}
