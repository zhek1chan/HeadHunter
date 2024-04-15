package ru.practicum.android.diploma.domain.favorite

interface DeleteVacancyRepository {
    suspend fun delete(data: String)
}
