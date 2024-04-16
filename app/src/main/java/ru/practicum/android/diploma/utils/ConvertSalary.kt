package ru.practicum.android.diploma.utils

import android.content.res.Resources
import android.util.Log
import ru.practicum.android.diploma.R

class ConvertSalary {
    private fun formatSalary(res: Resources, salaryFrom: String?, salaryTo: String?): String {
        Log.d("formating salary", "$salaryFrom  and  $salaryTo")
        return when {
            salaryFrom != "null" && (salaryTo == "null"
                || salaryTo == "0") -> res.getString(R.string.vacancy_salary_from, salaryFrom)

            (salaryFrom == "0" || salaryFrom == "null")
                && salaryTo != "null" && salaryTo != "0" -> res.getString(R.string.vacancy_salary_to, salaryTo)

            salaryFrom != "null" -> res.getString(R.string.vacancy_salary_from_to, salaryFrom, salaryTo)
            else -> {
                res.getString(R.string.vacancy_salary_not_specified)
            }
        }
    }

    fun formatSalaryWithCurrency(res: Resources, salaryFrom: String?, salaryTo: String?, currency: String?): String {
        val formattedSalary = formatSalary(res, salaryFrom, salaryTo)
        return when (currency) {
            res.getString(R.string.usd) -> res.getString(R.string.euro_get, formattedSalary)
            res.getString(R.string.eur) -> res.getString(R.string.dollar_get, formattedSalary)
            res.getString(R.string.rur) -> res.getString(R.string.rub_get, formattedSalary)
            res.getString(R.string.azn) -> res.getString(R.string.azn_get, formattedSalary)
            res.getString(R.string.byr) -> res.getString(R.string.br_get, formattedSalary)
            res.getString(R.string.gel) -> res.getString(R.string.gel_get, formattedSalary)
            res.getString(R.string.kgs) -> res.getString(R.string.kgs_get, formattedSalary)
            res.getString(R.string.kzt) -> res.getString(R.string.kzt_get, formattedSalary)
            res.getString(R.string.uah) -> res.getString(R.string.uah_get, formattedSalary)
            res.getString(R.string.uzs) -> res.getString(R.string.uzs_get, formattedSalary)
            else -> formattedSalary
        }
    }
}
