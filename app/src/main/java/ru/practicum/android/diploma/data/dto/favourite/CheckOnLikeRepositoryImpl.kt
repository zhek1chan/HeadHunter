package ru.practicum.android.diploma.data.dto.favourite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.data.converters.Convertors
import ru.practicum.android.diploma.data.dto.room.database.AppDatabase
import ru.practicum.android.diploma.domain.favorite.CheckOnLikeRepository
import ru.practicum.android.diploma.domain.models.DetailVacancy

class CheckOnLikeRepositoryImpl(val db: AppDatabase) : CheckOnLikeRepository {
    override suspend fun favouritesCheck(id: String): Flow<Boolean> = flow {
        emit(db.vacancyDao().queryVacancyId(id) != null)
    }

    override fun checkOnFavDB(id: String): Boolean {
        return db.vacancyDao().checkById(id) == 1
    }

    override fun getVacancy(id: String): DetailVacancy {
        return Convertors().convertorToDetailVacancyFromEntity((db.vacancyDao().getVacancyById(id)))
    }
}
