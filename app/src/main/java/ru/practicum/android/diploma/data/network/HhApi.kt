package ru.practicum.android.diploma.data.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.QueryMap
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.dto.fields.AreaDto
import ru.practicum.android.diploma.data.dto.fields.CountryDto
import ru.practicum.android.diploma.data.dto.fields.DetailVacancyDto
import ru.practicum.android.diploma.data.dto.fields.IndustryDto

interface HhApi {

    @Headers(
        "Authorization: Bearer ${BuildConfig.HH_ACCESS_TOKEN}",
        "User-Agent: VacancySearcher (evg.ermolov@yandex.ru)"
    )
    @GET("vacancies")
    suspend fun jobSearch(
        @QueryMap options: Map<String, String>
    ): SearchListDto

    @GET("vacancies/{vacancy_id}")
    suspend fun getDetailVacancy(@Path("vacancy_id") vacancyId: String): DetailVacancyDto

    @GET("areas")
    suspend fun filterRegions(): List<AreaDto>

    @GET("areas")
    suspend fun filterCountry(): List<CountryDto>

    @GET("areas/{area_id}")
    suspend fun filterRegion(@Path("area_id") areaId: String): AreaDto

    @GET("industries")
    suspend fun filterIndustry(): List<IndustryDto>
}
