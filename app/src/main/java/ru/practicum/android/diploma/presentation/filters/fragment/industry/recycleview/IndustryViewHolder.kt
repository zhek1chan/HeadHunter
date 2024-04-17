package ru.practicum.android.diploma.presentation.filters.fragment.industry.recycleview

import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.practicum.android.diploma.databinding.IndustryItemBinding
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.SubIndustry

class IndustryViewHolder(val binding: IndustryItemBinding) : ViewHolder(binding.root) {

    fun bind(item: SubIndustry) {
        binding.industry.text = item.name
    }
}
