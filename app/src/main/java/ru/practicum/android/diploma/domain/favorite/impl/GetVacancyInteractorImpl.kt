package ru.practicum.android.diploma.domain.favorite.impl

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.domain.favorite.GetVacancyInteractor
import ru.practicum.android.diploma.domain.favorite.GetVacancyRepository
import ru.practicum.android.diploma.domain.models.Vacancy

class GetVacancyInteractorImpl(val rep: GetVacancyRepository): GetVacancyInteractor {
    override fun getVacancy(): Flow<List<Vacancy>> {
        return rep.get()
    }
}
