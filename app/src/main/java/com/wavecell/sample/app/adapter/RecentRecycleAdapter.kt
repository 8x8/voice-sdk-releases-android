package com.wavecell.sample.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wavecell.sample.app.adapter.diff.RecentDiffUtil
import com.wavecell.sample.app.databinding.RowRecentCallBinding
import com.wavecell.sample.app.presentation.model.RowRecentCallViewModel

class RecentRecycleAdapter constructor(
        private val listener: ClickListener
): ListAdapter<RowRecentCallViewModel, RecentRecycleAdapter.ViewHolder>(RecentDiffUtil()) {

    interface ClickListener {
        fun onClick(userId: String)
    }

    /**
     * List Adapter Implementation
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowRecentCallBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    /**
     * View Holder
     */

    inner class ViewHolder(private val binding: RowRecentCallBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: RowRecentCallViewModel) {
            binding.viewModel = viewModel
            binding.clickListener = listener
            binding.executePendingBindings()
        }
    }
}