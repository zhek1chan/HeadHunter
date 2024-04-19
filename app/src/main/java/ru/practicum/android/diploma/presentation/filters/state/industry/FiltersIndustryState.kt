package ru.practicum.android.diploma.presentation.filters.state.industry

import ru.practicum.android.diploma.domain.models.SubIndustry

sealed interface FiltersIndustryState {
    data class CurrentIndustry(val industry: SubIndustry?) : FiltersIndustryState
    data class IndustryId(val id: String?) : FiltersIndustryState
    data class IndustryIndex(val index: Int) : FiltersIndustryState
}
