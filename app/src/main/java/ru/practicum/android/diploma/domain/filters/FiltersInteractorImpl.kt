package ru.practicum.android.diploma.domain.filters

import kotlinx.coroutines.flow.Flow
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.domain.models.Industry

class FiltersInteractorImpl(
    private val filtersRepository: FiltersRepository
) : FiltersInteractor {

    override fun getPrefs(): Filters = filtersRepository.getPrefs()

    override fun savePrefs(settings: Filters) {
        filtersRepository.savePrefs(settings)
    }

    override fun clearPrefs() {
        filtersRepository.clearPrefs()
    }

    override suspend fun getCountries(): Flow<Resource<List<Country>>> {
        return filtersRepository.getCountries()
    }

    override suspend fun getRegions(idArea: String?): Flow<Resource<List<Area>>> {
        return filtersRepository.getRegions(idArea)
    }

    override suspend fun getIndustries(): Flow<Resource<List<Industry>>> {
        return filtersRepository.getIndustries()
    }
}
