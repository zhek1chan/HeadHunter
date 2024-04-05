package ru.practicum.android.diploma.domain.models

data class VacancyData(
    val found: Int,
    val page: Int,
    val listVacancy: List<Vacancy>
)
