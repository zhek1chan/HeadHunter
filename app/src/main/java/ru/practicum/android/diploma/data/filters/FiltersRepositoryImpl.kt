package ru.practicum.android.diploma.data.filters

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.data.Constant
import ru.practicum.android.diploma.data.converters.Convertors
import ru.practicum.android.diploma.data.network.CountriesRequest
import ru.practicum.android.diploma.data.network.IndustriesRequest
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RegionsRequest
import ru.practicum.android.diploma.domain.filters.FiltersRepository
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.Filters
import ru.practicum.android.diploma.domain.models.Industry

class FiltersRepositoryImpl(
    private val filtersStorageImpl: FiltersStorageRepositoryImpl,
    private val networkClient: NetworkClient
) : FiltersRepository {

    override fun getPrefs(): Filters = filtersStorageImpl.getPrefs()

    override fun savePrefs(settings: Filters) {
        filtersStorageImpl.savePrefs(settings)
    }

    override fun clearPrefs() {
        filtersStorageImpl.clearPrefs()
    }

    override suspend fun getCountries(): Flow<Resource<List<Country>>> = flow {
        val response = networkClient.doRequest(CountriesRequest)
        when (response.resultCode) {
            Constant.NO_CONNECTIVITY_MESSAGE -> {
                emit(Resource(data = null, code = Constant.NO_CONNECTIVITY_MESSAGE))
            }

            Constant.SUCCESS_RESULT_CODE -> {
                emit(
                    Resource(
                        data = response.countriesList.map { countryDto -> Convertors().convertorToCountry(countryDto) },
                        code = Constant.SUCCESS_RESULT_CODE
                    )
                )
            }

            else -> {
                emit(Resource(data = null, code = Constant.SERVER_ERROR))
            }
        }
    }

    override suspend fun getRegions(idArea: String?): Flow<Resource<List<Area>>> = flow {
        val response = networkClient.doRequest(RegionsRequest(idArea))
        when (response.resultCode) {
            Constant.NO_CONNECTIVITY_MESSAGE -> {
                emit(Resource(data = null, code = Constant.NO_CONNECTIVITY_MESSAGE))
            }

            Constant.SUCCESS_RESULT_CODE -> {
                emit(
                    Resource(
                        data = response.regionsList.map { areaDto -> Convertors().convertorToArea(areaDto) },
                        code = Constant.SUCCESS_RESULT_CODE
                    )
                )
            }

            else -> {
                emit(Resource(data = null, code = Constant.SERVER_ERROR))
            }
        }
    }

    override suspend fun getIndustries(): Flow<Resource<List<Industry>>> = flow {
        val response = networkClient.doRequest(IndustriesRequest)
        when (response.resultCode) {
            Constant.NO_CONNECTIVITY_MESSAGE -> {
                emit(Resource(data = null, code = Constant.NO_CONNECTIVITY_MESSAGE))
            }

            Constant.SUCCESS_RESULT_CODE -> {
                emit(
                    Resource(
                        data = response.industriesList.map { industryDto ->
                            Convertors().convertorToIndustry(
                                industryDto
                            )
                        },
                        code = Constant.SUCCESS_RESULT_CODE
                    )
                )
            }

            else -> {
                emit(Resource(data = null, code = Constant.SERVER_ERROR))
            }
        }
    }
}
