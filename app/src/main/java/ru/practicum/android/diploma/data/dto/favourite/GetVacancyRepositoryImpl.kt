package ru.practicum.android.diploma.data.dto.favourite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.practicum.android.diploma.data.converters.VacancyShortMapper
import ru.practicum.android.diploma.data.dto.room.database.AppDatabase
import ru.practicum.android.diploma.domain.favorite.GetVacancyRepository
import ru.practicum.android.diploma.domain.models.Vacancy

class GetVacancyRepositoryImpl(
    private val db: AppDatabase
) : GetVacancyRepository {

    override fun get(): Flow<List<Vacancy>> =
        db.vacancyDao().getVacancyList().map { list ->
            list.map { item ->
                VacancyShortMapper.map(item)
            }
        }
}
