package ru.practicum.android.diploma.domain.search

import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.domain.models.VacancyData

interface SearchInteractor {
    suspend fun search(
        expression: String,
        page: Int,
    ): Resource<VacancyData>
}
