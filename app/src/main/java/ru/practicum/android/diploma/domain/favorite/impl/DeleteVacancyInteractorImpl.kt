package ru.practicum.android.diploma.domain.favorite.impl

import ru.practicum.android.diploma.domain.favorite.DeleteVacancyInteractor
import ru.practicum.android.diploma.domain.favorite.DeleteVacancyRepository

class DeleteVacancyInteractorImpl(private val rep: DeleteVacancyRepository) : DeleteVacancyInteractor {
    override suspend fun deleteVacancy(data: String) {
        rep.delete(data)
    }
}
