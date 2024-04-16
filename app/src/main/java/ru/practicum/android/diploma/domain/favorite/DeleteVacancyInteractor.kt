package ru.practicum.android.diploma.domain.favorite

interface DeleteVacancyInteractor {
    suspend fun deleteVacancy(data: String)
}
