package ru.practicum.android.diploma.presentation.filters.fragment.industry.recycleview

import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.practicum.android.diploma.databinding.IndustryItemBinding
import ru.practicum.android.diploma.domain.models.Industry

class IndustryViewHolder(val binding: IndustryItemBinding) : ViewHolder(binding.root) {

    var industryRadioBtn: RadioButton = binding.radioButton

    fun bind(item: Industry) {
        binding.industry.text = item.name
        binding.radioButton.isChecked = item.isChosen
    }
}
