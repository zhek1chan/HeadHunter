package ru.practicum.android.diploma.data.dto.favourite

import ru.practicum.android.diploma.data.converters.VacancyConverter
import ru.practicum.android.diploma.data.dto.room.VacancyDetails
import ru.practicum.android.diploma.data.dto.room.database.AppDatabase
import ru.practicum.android.diploma.domain.favorite.SaveVacancyRepository

class SaveVacancyRepositoryImpl(
    private val db: AppDatabase,
) : SaveVacancyRepository {

    override suspend fun save(data: VacancyDetails) {
        data?.run {
            db.vacancyDao().saveVacancy(VacancyConverter.map(this))
        }
    }

}
