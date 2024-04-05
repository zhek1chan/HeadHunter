package ru.practicum.android.diploma.data.dto.fields

import ru.practicum.android.diploma.data.dto.Response

data class IndustryDto(
    val id: String,
    val industries: List<SubIndustryDto>,
    val name: String
) : Response()
