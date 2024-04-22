package ru.practicum.android.diploma.presentation.filters.state.industry

import ru.practicum.android.diploma.presentation.filters.fragment.industry.recycleview.IndustriesAdapterItem

sealed interface RequestIndustriesState {
    data object Loading : RequestIndustriesState
    data object Error : RequestIndustriesState
    data object Empty : RequestIndustriesState

    data class Success(val data: List<IndustriesAdapterItem>) : RequestIndustriesState
}

