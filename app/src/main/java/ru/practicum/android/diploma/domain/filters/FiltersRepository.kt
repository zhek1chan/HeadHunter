package ru.practicum.android.diploma.domain.filters

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.domain.models.Industry

interface FiltersRepository {
    fun getPrefs(): Filters
    fun savePrefs(settings: Filters)
    fun clearPrefs()
    suspend fun getCountries(): Flow<Resource<List<Country>>>
    suspend fun getRegions(idArea: String?): Flow<Resource<List<Area>>>
    suspend fun getIndustries(): Flow<Resource<List<Industry>>>
}
