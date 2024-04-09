package ru.practicum.android.diploma.domain.search.impl

import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.domain.models.VacancyData
import ru.practicum.android.diploma.domain.search.SearchInteractor
import ru.practicum.android.diploma.domain.search.SearchRepository

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {
    override suspend fun search(expression: String, page: Int): Resource<VacancyData> {
        return searchRepository.search(expression, page)
    }

}
