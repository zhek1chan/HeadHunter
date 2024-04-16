package ru.practicum.android.diploma.domain.favorite

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.Vacancy

interface GetVacancyInteractor {
    fun getVacancy(): Flow<List<Vacancy>>

}
