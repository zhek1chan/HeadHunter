package ru.practicum.android.diploma.domain.favorite

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.data.dto.room.VacancyDetails

interface GetDataByIdRepository {
    fun getById(id: String): Flow<Resource<VacancyDetails>>
}
