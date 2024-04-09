package ru.practicum.android.diploma.data.search

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.practicum.android.diploma.common.Resource
import ru.practicum.android.diploma.data.Constant
import ru.practicum.android.diploma.data.converters.Convertors
import ru.practicum.android.diploma.data.dto.fields.DetailVacancyDto
import ru.practicum.android.diploma.data.filters.FiltersStorageRepositoryImpl
import ru.practicum.android.diploma.data.network.DetailVacancyRequest
import ru.practicum.android.diploma.data.network.JobSearchRequest
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.SearchListDto
import ru.practicum.android.diploma.domain.models.DetailVacancy
import ru.practicum.android.diploma.domain.models.VacancyData
import ru.practicum.android.diploma.domain.search.SearchRepository

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val filtersStorageImpl: FiltersStorageRepositoryImpl
) : SearchRepository {

    override suspend fun search(expression: String, page: Int): Resource<VacancyData> {
        val prefs = filtersStorageImpl.getPrefs()
        val countryId = prefs.countryId
        val regionId = prefs.regionId
        val industryId = prefs.industryId
        val salary = prefs.expectedSalary
        val salaryOnly = prefs.salaryOnlyCheckbox
        val options = mutableMapOf<String, String>()

        if (countryId.isNotEmpty()) {
            if (regionId.isNotEmpty()) {
                options[Constant.AREA] = regionId
            } else {
                options[Constant.AREA] = countryId
            }
        }

        if (industryId.isNotEmpty()) {
            options[Constant.INDUSTRY] = industryId
        }

        if (salary.isNotEmpty()) {
            options[Constant.SALARY] = salary
        }

        options[Constant.ONLY_WITH_SALARY] = salaryOnly.toString()
        options[Constant.PAGE] = page.toString()
        options[Constant.PER_PAGE] = Constant.PER_PAGE_ITEMS
        options[Constant.TEXT] = expression
        val response = networkClient.search(JobSearchRequest(options))
        return when (response.resultCode) {
            Constant.NO_CONNECTIVITY_MESSAGE -> {
                Resource(code = Constant.NO_CONNECTIVITY_MESSAGE)
            }
            Constant.SUCCESS_RESULT_CODE -> {
                val result = response as SearchListDto
                Resource(
                    VacancyData(
                        found = result.found ?: 0,
                        page = result.page ?: 0,
                        listVacancy = result.results.map { vacancyDto ->
                            Convertors().convertorToVacancy(vacancyDto)
                        }
                    ),
                    Constant.SUCCESS_RESULT_CODE
                )
            }
            else -> {
                Resource(code = Constant.SERVER_ERROR)
            }
        }
    }

    override suspend fun getDetailVacancy(id: String): Flow<Resource<DetailVacancy>> = flow {
        val response = networkClient.doRequest(DetailVacancyRequest(id))
        networkClient.doRequest(DetailVacancyRequest(id))
        when (response.resultCode) {
            Constant.NO_CONNECTIVITY_MESSAGE -> {
                emit(Resource(code = Constant.NO_CONNECTIVITY_MESSAGE))
            }

            Constant.SUCCESS_RESULT_CODE -> {
                emit(Resource(data = Convertors().convertorToDetailVacancy(response as DetailVacancyDto)))
            }

            else -> {
                emit(Resource(code = Constant.SERVER_ERROR))
            }
        }
    }
}
