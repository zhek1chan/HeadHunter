package ru.practicum.android.diploma.presentation.favourite.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.utils.ConvertSalary
import java.util.Locale

class VacancyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val convertSalary = ConvertSalary()
    private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
    private val tvCompanyName: TextView = itemView.findViewById(R.id.department)
    private val tvSalary: TextView = itemView.findViewById(R.id.salary)
    private val ivUrl100: ImageView = itemView.findViewById(R.id.iv_company)
    private val radius = itemView.resources.getDimensionPixelSize(R.dimen.corner_radius_card_company_12)

    @SuppressLint("SetTextI18n")
    fun bind(item: Vacancy) {
        tvDescription.text = item.name
        tvCompanyName.text = item.employer
        var salaryFrom = "%,d".format(Locale.US, item.salaryFrom)
        var salaryTo = "%,d".format(Locale.US, item.salaryTo)
        salaryFrom = salaryFrom.replace(',', ' ')
        salaryTo = salaryTo.replace(',', ' ')
        tvSalary.text = convertSalary.formatSalaryWithCurrency(salaryFrom, salaryTo, item.currency)
        Glide.with(ivUrl100)
            .load(item.employerLogoUrls)
            .placeholder(R.drawable.placeholder_vacancy)
            .transform(RoundedCorners(radius))
            .into(ivUrl100)
    }
}
