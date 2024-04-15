package ru.practicum.android.diploma.domain.favorite

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Vacancy

interface GetVacancyRepository {
    fun get(): Flow<List<Vacancy>>
}
