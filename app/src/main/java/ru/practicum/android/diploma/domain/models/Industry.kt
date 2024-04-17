package ru.practicum.android.diploma.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Industry(
    val id: String,
    val industries: List<SubIndustry>,
    val name: String,
    var isChosen: Boolean
) : Parcelable
