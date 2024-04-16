package ru.practicum.android.diploma.domain.favorite.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.favorite.CheckVacancyOnLikeInteractor
import ru.practicum.android.diploma.domain.favorite.CheckVacancyOnLikeRepository
import ru.practicum.android.diploma.domain.models.DetailVacancy

class CheckVacancyOnLikeInteractorImpl(private val rep: CheckVacancyOnLikeRepository) : CheckVacancyOnLikeInteractor {
    override suspend fun favouritesCheck(id: String): Flow<Boolean> {
        return rep.favouritesCheck(id)
    }

    override fun checkOnFavDB(id: String): Boolean {
        return rep.checkOnFavDB(id)
    }

    override fun getVacancy(id: String): DetailVacancy {
        return rep.getVacancy(id)
    }
}
