package ru.practicum.android.diploma.presentation.filters.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.databinding.CountryItemBinding
import ru.practicum.android.diploma.domain.models.RegionDataItem
import ru.practicum.android.diploma.presentation.filters.adapter.viewholder.RegionViewHolder

class RegionAdapter(private val clickListener: RegionClickListener) : RecyclerView.Adapter<RegionViewHolder>() {
    private var regionsList = listOf<RegionDataItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return RegionViewHolder(
            CountryItemBinding.inflate(
                layoutInspector,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.bind(regionsList[position].currentRegion)
        holder.itemView.setOnClickListener { clickListener.onRegionClick(regionsList[position]) }
    }

    override fun getItemCount(): Int {
        return regionsList.size
    }

    fun interface RegionClickListener {
        fun onRegionClick(region: RegionDataItem)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newList: List<RegionDataItem>) {
        regionsList = newList
        notifyDataSetChanged()
    }
}
