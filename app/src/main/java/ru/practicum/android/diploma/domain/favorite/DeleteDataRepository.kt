package ru.practicum.android.diploma.domain.favorite

interface DeleteDataRepository {
    suspend fun delete(data: String)
}
