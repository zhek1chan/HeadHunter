package ru.practicum.android.diploma.utils

import android.util.Log

class ConvertSalary {
    private fun formatSalary(salaryFrom: String?, salaryTo: String?): String {
        Log.d("formating salary", "$salaryFrom  and  $salaryTo")
        return when {
            salaryFrom != "null" && (salaryTo == "null"
                || salaryTo == "0") -> "от $salaryFrom"
            (salaryFrom == "0" || salaryFrom == "null")
                && salaryTo != "null" && salaryTo != "0" -> "до $salaryTo"
            salaryFrom != "null" -> "от $salaryFrom до $salaryTo"
            else -> {
                "Зарплата не указана"
            }
        }
    }

    fun formatSalaryWithCurrency(salaryFrom: String?, salaryTo: String?, currency: String?): String {
        val formattedSalary = formatSalary(salaryFrom, salaryTo)
        return when (currency) {
            "USD" -> "$formattedSalary €"
            "EUR" -> "$formattedSalary $"
            "RUR" -> "$formattedSalary ₽"
            "AZN" -> "$formattedSalary ₼"
            "BYR" -> "$formattedSalary Br"
            "GEL" -> "$formattedSalary ₾"
            "KGS" -> "$formattedSalary с"
            "KZT" -> "$formattedSalary ₸"
            "UAH" -> "$formattedSalary ₴"
            "UZS" -> "$formattedSalary Soʻm"
            else -> formattedSalary
        }
    }
}
