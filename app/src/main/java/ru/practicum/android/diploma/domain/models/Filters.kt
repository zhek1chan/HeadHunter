package ru.practicum.android.diploma.domain.models

data class Filters(
    val country: String,
    val countryId: String,
    val region: String,
    val regionId: String,
    val industry: String,
    val industryId: String,
    val expectedSalary: String,
    val salaryOnlyCheckbox: Boolean
)

fun Filters.checkEmpty(): Boolean {
    return countryId.isEmpty() && regionId.isEmpty() && industry.isEmpty()
        && industryId.isEmpty() && expectedSalary.isEmpty() && !salaryOnlyCheckbox
}
