package ru.practicum.android.diploma.data.converters

import ru.practicum.android.diploma.data.dto.room.database.VacancyEntity
import ru.practicum.android.diploma.domain.models.Vacancy

object VacancyShortMapper {
    fun map(v: VacancyEntity): Vacancy = Vacancy(
        id = v.id,
        city = v.area,
        employerLogoUrls = v.logoUrl,
        employer = v.employerName,
        name = v.name,
        salaryTo = v.salaryTo,
        salaryFrom = v.salaryFrom,
        currency = v.salaryCurrency
    )
}
