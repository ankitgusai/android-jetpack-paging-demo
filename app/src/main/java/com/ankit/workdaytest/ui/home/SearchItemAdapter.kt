package com.ankit.workdaytest.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ankit.workdaytest.R
import com.ankit.workdaytest.databinding.ItemSearchImageBinding
import com.ankit.workdaytest.networking.models.Item
import com.bumptech.glide.Glide

class SearchItemAdapter(private val context: Context, private val itemClicked: (item: Item) -> Unit) :
    PagingDataAdapter<Item, SearchItemAdapter.ItemViewHolder>(ItemComparator) {
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItem(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val viewHolder = ItemSearchImageBinding.inflate(LayoutInflater.from(context), parent, false)
        return ItemViewHolder(viewHolder)
    }

    inner class ItemViewHolder(private val itemBinding: ItemSearchImageBinding) : RecyclerView.ViewHolder(itemBinding.root), OnClickListener {
        var item: Item? = null

        init {
            itemBinding.root.setOnClickListener(this@ItemViewHolder)
        }

        fun bindItem(item: Item) {
            this.item = item
            itemBinding.tvTitle.text = item.data[0].title
            itemBinding.tvDate.text = item.data[0].date_created
            Glide.with(itemBinding.ivThumb.context)
                .load(item.links[0].href) // assuming `Item` has a property `imageUrl`
                .placeholder(R.drawable.image_placeholder_24) // optional placeholder
                .error(R.drawable.error_image_24) // optional error image
                .into(itemBinding.ivThumb)
        }

        override fun onClick(v: View?) {
            item?.also {
                itemClicked(it)
            }
        }
    }
}


object ItemComparator : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        // Id is a unique identifier for a Item.
        // You may have something different.
        // This checks if the items are the same.
        return oldItem.data[0].nasa_id == newItem.data[0].nasa_id
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
        // This checks if the contents of the items are the same.
        // It's possible you have to check deeper if you have a complex object.
        return oldItem == newItem
    }
}