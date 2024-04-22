package ru.practicum.android.diploma.domain.favorite

import ru.practicum.android.diploma.data.dto.room.VacancyDetails

interface SaveVacancyRepository {

    suspend fun save(data: VacancyDetails)
}
