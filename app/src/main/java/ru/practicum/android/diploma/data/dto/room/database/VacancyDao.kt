package ru.practicum.android.diploma.data.dto.room.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VacancyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveVacancy(entity: VacancyEntity)

    @Query("DELETE FROM favourites_table WHERE id = :vacancyId")
    suspend fun deleteVacancy(vacancyId: String)

    @Query("SELECT * FROM favourites_table")
    fun getVacancyList(): Flow<List<VacancyEntity>>

    @Query("SELECT * FROM favourites_table WHERE id = :vacancyId LIMIT 1")
    fun getVacancyById(vacancyId: String): VacancyEntity

    @Query("SELECT * FROM favourites_table WHERE id=:searchId")
    fun queryVacancyId(searchId: String): VacancyEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favourites_table WHERE id=:id)")
    fun checkById(id: String): Int
    @Update
    suspend fun updateVacancy(entity: VacancyEntity)
}
