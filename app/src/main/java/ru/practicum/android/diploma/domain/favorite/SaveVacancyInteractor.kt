package ru.practicum.android.diploma.domain.favorite

import ru.practicum.android.diploma.data.dto.room.VacancyDetails

interface SaveVacancyInteractor {
    suspend fun saveVacancy(data: VacancyDetails)
}
