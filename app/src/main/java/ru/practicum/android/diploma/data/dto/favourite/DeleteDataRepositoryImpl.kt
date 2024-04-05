package ru.practicum.android.diploma.data.dto.favourite

import ru.practicum.android.diploma.data.dto.room.database.AppDatabase
import ru.practicum.android.diploma.domain.favorite.DeleteDataRepository

class DeleteDataRepositoryImpl(private val db: AppDatabase) : DeleteDataRepository {
    override suspend fun delete(data: String) {
        db.vacancyDao().deleteVacancy(data)
    }
}
