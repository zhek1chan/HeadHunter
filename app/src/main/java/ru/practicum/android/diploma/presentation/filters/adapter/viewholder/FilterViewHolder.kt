package ru.practicum.android.diploma.presentation.filters.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.CountryItemBinding
import ru.practicum.android.diploma.domain.models.Country

class FilterViewHolder(private val binding: CountryItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Country) {
        binding.country.text = item.name
    }
}
