package ru.practicum.android.diploma.data.dto.favourite

import ru.practicum.android.diploma.data.dto.room.database.AppDatabase
import ru.practicum.android.diploma.domain.favorite.DeleteVacancyRepository

class DeleteVacancyRepositoryImpl(private val db: AppDatabase) : DeleteVacancyRepository {
    override suspend fun delete(data: String) {
        db.vacancyDao().deleteVacancy(data)
    }
}
