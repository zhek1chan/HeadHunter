package ru.practicum.android.diploma.data.dto

import ru.practicum.android.diploma.data.dto.fields.AreaDto
import ru.practicum.android.diploma.data.dto.fields.CountryDto
import ru.practicum.android.diploma.data.dto.fields.IndustryDto

open class Response {
    var resultCode = 0
    var countriesList: List<CountryDto> = emptyList()
    var regionsList: List<AreaDto> = emptyList()
    var industriesList: List<IndustryDto> = emptyList()
}
