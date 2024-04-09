package ru.practicum.android.diploma.presentation.search.adapter

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class SearchLoadStateAdapter : LoadStateAdapter<ProgressBarViewHolder>() {
    override fun onBindViewHolder(holder: ProgressBarViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ProgressBarViewHolder {
        return ProgressBarViewHolder.create(parent)
    }
}
