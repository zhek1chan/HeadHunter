package ru.practicum.android.diploma.presentation.filters.fragment.industry.recycleview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.IndustryItemBinding
import ru.practicum.android.diploma.domain.models.Industry

class IndustriesAdapter : RecyclerView.Adapter<IndustryViewHolder>() {

    var industryList: List<Industry> = emptyList()
    private var clickListener: IndustryClick? = null

    fun setIndustryClickListener(listener: IndustryClick) {
        clickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndustryViewHolder {
        return IndustryViewHolder(IndustryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return industryList.size
    }

    override fun onBindViewHolder(holder: IndustryViewHolder, position: Int) {
        val item = industryList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            clickListener?.click(item)
        }

        holder.industryRadioBtn.setOnClickListener {
            clickListener?.click(item)
        }
    }
}
