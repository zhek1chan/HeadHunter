package ru.practicum.android.diploma.domain.favorite.impl

import ru.practicum.android.diploma.data.dto.room.VacancyDetails
import ru.practicum.android.diploma.domain.favorite.SaveVacancyInteractor
import ru.practicum.android.diploma.domain.favorite.SaveVacancyRepository

class SaveVacancyInteractorImpl(private val rep: SaveVacancyRepository) : SaveVacancyInteractor {
    override suspend fun saveVacancy(data: VacancyDetails) {
        rep.save(data)
    }

}
