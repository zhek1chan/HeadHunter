package ru.practicum.android.diploma.presentation.filters.fragment.industry

import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.presentation.filters.fragment.industry.recycleview.IndustriesAdapterItem

sealed interface RequestIndustriesState {
    object Loading : RequestIndustriesState
    object Error : RequestIndustriesState
    object Empty : RequestIndustriesState

    data class Success(val data: List<IndustriesAdapterItem>) : RequestIndustriesState
}

