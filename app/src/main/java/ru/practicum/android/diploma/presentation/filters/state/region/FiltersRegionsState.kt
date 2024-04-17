package ru.practicum.android.diploma.presentation.filters.state.region

import ru.practicum.android.diploma.domain.models.RegionDataItem

sealed interface FiltersRegionsState {
    object Loading : FiltersRegionsState
    data class Content(val regions: List<RegionDataItem>) : FiltersRegionsState
    object Error : FiltersRegionsState
    object Empty : FiltersRegionsState

    object Start : FiltersRegionsState
}
