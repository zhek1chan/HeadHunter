package ru.practicum.android.diploma.presentation.filters.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.CountryItemBinding
import ru.practicum.android.diploma.domain.models.Area
class RegionViewHolder(private val binding: CountryItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Area) {
        binding.country.text = item.name
    }
}
