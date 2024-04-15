package ru.practicum.android.diploma.domain.favorite

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.models.DetailVacancy

interface CheckVacancyOnLikeRepository {
    suspend fun favouritesCheck(id: String): Flow<Boolean>

    fun checkOnFavDB(id: String): Boolean
    fun getVacancy(id: String): DetailVacancy
}
