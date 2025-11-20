package com.tohir.booksplusplus.ui.books

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tohir.booksplusplus.databinding.ItemContentsBinding
import org.readium.r2.shared.publication.Link

class ContentsAdapter(private val listener: OnTableOfContentsClickListener) : RecyclerView.Adapter<ContentsAdapter.ViewHolder>() {

    private var toc = listOf<Link>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(ItemContentsBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(toc[position])
    }

    override fun getItemCount(): Int {
        return toc.size
    }

    fun setToc(toc: List<Link>) {
        this.toc = toc
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: ItemContentsBinding ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(link: Link) {
            binding.textViewItemContents.text = link.title

            binding.textViewItemContents.setOnClickListener {
                listener.onTableOfContentClicked(link)
            }
        }

    }

    interface OnTableOfContentsClickListener {
        fun onTableOfContentClicked(link: Link)

    }

}