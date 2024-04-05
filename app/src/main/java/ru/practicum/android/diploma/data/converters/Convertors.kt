package ru.practicum.android.diploma.data.converters

import ru.practicum.android.diploma.data.dto.fields.AreaDto
import ru.practicum.android.diploma.data.dto.fields.CountryDto
import ru.practicum.android.diploma.data.dto.fields.DetailVacancyDto
import ru.practicum.android.diploma.data.dto.fields.EmployerDto
import ru.practicum.android.diploma.data.dto.fields.IndustryDto
import ru.practicum.android.diploma.data.dto.fields.KeySkillsDto
import ru.practicum.android.diploma.data.dto.fields.PhoneNumsDto
import ru.practicum.android.diploma.data.dto.fields.VacancyDto
import ru.practicum.android.diploma.data.dto.room.database.VacancyEntity
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country
import ru.practicum.android.diploma.domain.models.DetailVacancy
import ru.practicum.android.diploma.domain.models.Industry
import ru.practicum.android.diploma.domain.models.SubIndustry
import ru.practicum.android.diploma.domain.models.Vacancy

class Convertors {
    fun convertorToVacancy(vacancy: VacancyDto): Vacancy {
        return Vacancy(
            id = vacancy.id,
            city = createAreaName(vacancy.area),
            employerLogoUrls = vacancy.employer.logoUrlsDto?.art240,
            employer = createEmployerName(vacancy.employer),
            name = vacancy.name,
            currency = vacancy.salary?.currency,
            salaryFrom = vacancy.salary?.from,
            salaryTo = vacancy.salary?.to,
        )
    }

    fun convertorToDetailVacancy(vacancy: DetailVacancyDto): DetailVacancy {
        var contactComments = ""
        vacancy.contacts?.phones?.forEach {
            if (it.comment?.isNotEmpty() == true) {
                contactComments += "${it.comment}\n"
            }
        }
        return DetailVacancy(
            id = vacancy.id,
            areaId = "",
            areaName = createAreaName(vacancy.area),
            areaUrl = vacancy.employer?.logoUrlsDto?.art240,
            contactsCallTrackingEnabled = false,
            contactsEmail = vacancy.contacts?.email,
            contactsName = vacancy.contacts?.name,
            contactsPhones = vacancy.contacts?.phones.let { list -> list?.map { createPhone(it) } },
            comment = contactComments,
            description = vacancy.description,
            employerName = createEmployerName(vacancy.employer),
            employmentId = "",
            employmentName = vacancy.employment.name,
            experienceId = "",
            experienceName = vacancy.experience.name,
            keySkillsNames = createKeySkills(vacancy.keySkills),
            name = vacancy.name,
            salaryCurrency = vacancy.salary?.currency,
            salaryFrom = vacancy.salary?.from,
            salaryGross = false,
            salaryTo = vacancy.salary?.to,
            scheduleId = "",
            scheduleName = vacancy.schedule?.name,
            logoUrl = vacancy.employer?.logoUrlsDto?.original,
            logoUrl90 = vacancy.employer?.logoUrlsDto?.art90,
            logoUrl240 = vacancy.employer?.logoUrlsDto?.art240,
            employerUrl = vacancy.employer?.logoUrlsDto?.art240,
            url = vacancy.url
        )
    }

    fun convertorToDetailVacancyFromEntity(vacancy: VacancyEntity): DetailVacancy {
        return DetailVacancy(
            id = vacancy.id,
            areaId = "",
            areaName = vacancy.area,
            areaUrl = vacancy.logoUrl240,
            contactsCallTrackingEnabled = false,
            contactsEmail = vacancy.contactEmail,
            contactsName = vacancy.contactName,
            contactsPhones = phoneConvertion(vacancy.phones.toString()),
            comment = vacancy.contactComment,
            description = vacancy.description,
            employerName = vacancy.employerName,
            employmentId = "",
            employmentName = vacancy.employment,
            experienceId = "",
            experienceName = vacancy.experience,
            keySkillsNames = keySkillConvertion(vacancy.keySkills.toString()),
            name = vacancy.name,
            salaryCurrency = vacancy.salaryCurrency,
            salaryFrom = vacancy.salaryFrom,
            salaryGross = false,
            salaryTo = vacancy.salaryTo,
            scheduleId = "",
            scheduleName = vacancy.schedule,
            logoUrl = vacancy.logoUrl,
            logoUrl90 = vacancy.logoUrl90,
            logoUrl240 = vacancy.logoUrl240,
            employerUrl = vacancy.logoUrl240,
            url = vacancy.url
        )
    }

    fun convertorToCountry(country: CountryDto): Country {
        return Country(
            id = country.id,
            name = country.name ?: "",
            parentId = country.parentId ?: "",
        )
    }

    private fun convertorToArea(region: AreaDto): Area {
        return Area(
            id = region.id,
            name = region.name ?: "",
            parentId = region.parentId ?: "",
            areas = region.areas.map { convertorToArea(it) }
        )
    }

    fun convertorToIndustry(industry: IndustryDto): Industry {
        val subindustries: MutableList<SubIndustry> = mutableListOf()
        for (subindustry in industry.industries) {
            subindustries.add(
                SubIndustry(
                    id = subindustry.id,
                    name = subindustry.name
                )
            )
        }
        return Industry(
            id = industry.id,
            name = industry.name,
            industries = subindustries
        )
    }

    private fun createAreaName(area: CountryDto?): String? {
        return if (area?.name == null) {
            null
        } else {
            area.name
        }
    }

    private fun createEmployerName(employerName: EmployerDto?): String? {
        return if (employerName?.name == null) {
            null
        } else {
            employerName.name
        }
    }

    private fun createPhone(phone: PhoneNumsDto): String {
        return "+${phone.country}" + " (${phone.city})" + " ${phone.number}"
    }

    private fun phoneConvertion(number: String): List<String>? {
        var i = 0
        var phoneNum: String? = null
        var phoneNumList: List<String>? = null
        number.forEach {
            if (it != ',') {
                i = +1
                phoneNum += it
            } else {
                phoneNumList?.plus(phoneNum)
            }
        }
        return if (i == 0) {
            null
        } else {
            phoneNumList
        }
    }

    private fun keySkillConvertion(kS: String): List<String>? {
        var i = 0
        var skill: String? = null
        var skillsList: List<String>? = null
        kS.forEach {
            if (it != ',') {
                i = +1
                skill += it
            } else {
                skillsList?.plus(skill)
            }
        }
        return if (i == 0) {
            null
        } else {
            skillsList
        }
    }

    private fun createKeySkills(keySkills: List<KeySkillsDto>?): List<String>? {
        var i = 0
        keySkills?.forEach {
            if (it.toString().isNotEmpty()) {
                i = +1
            }
        }
        if (i == 0) {
            return null
        }
        return listOf((keySkills?.map { it.name } ?: emptyList()).toString())
    }

}
