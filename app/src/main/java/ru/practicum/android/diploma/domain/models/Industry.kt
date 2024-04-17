package ru.practicum.android.diploma.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Industry(
    val id: String,
    val industries: List<SubIndustry>,
    val name: String,
) : Parcelable {
    val isChosen: IsChosenClass = IsChosenClass()
}
class IsChosenClass {
    var isChosen: Boolean = false
}
