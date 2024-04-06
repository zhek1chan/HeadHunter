package ru.practicum.android.diploma.presentation.vacancy.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentVacancyBinding
import ru.practicum.android.diploma.domain.models.DetailVacancy
import ru.practicum.android.diploma.domain.models.Vacancy
import ru.practicum.android.diploma.presentation.vacancy.state.VacancyState
import ru.practicum.android.diploma.presentation.vacancy.viewmodel.VacancyDetailViewModel
import ru.practicum.android.diploma.utils.ConvertSalary

class VacancyDetailFragment : Fragment() {

    private var vacancyId: String? = null
    private var _vacancy: DetailVacancy? = null
    private var _binding: FragmentVacancyBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<VacancyDetailViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacancyBinding.inflate(inflater, container, false)
        binding.vacancyToolbars.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vacancyId = requireArguments().getString(ARGS_VACANCY)
        vacancyId = arguments?.getParcelable<Vacancy>("vacancyId")!!.id
        viewModel.getVacancyDetail(vacancyId!!)
        viewModel.vacancyState.observe(viewLifecycleOwner) { state ->
            render(state)
        }
        binding.buttonSimilar.setOnClickListener {
            val vacancyId = it.id.toString()
        }
        if (vacancyId == null) {
            onDestroy()
        }
        viewModel.onLikedCheck(vacancyId!!).observe(requireActivity()) { likeIndicator ->
            if (!likeIndicator) {
                binding.buttonAddToFavorites.visibility = View.VISIBLE
                binding.buttonDeleteFromFavorites.visibility = View.GONE
                _vacancy?.isFavorite?.isFavorite = false
                binding.buttonAddToFavorites.setOnClickListener {
                    viewModel.clickOnButton()
                }
            } else {
                binding.buttonAddToFavorites.visibility = View.GONE
                binding.buttonDeleteFromFavorites.visibility = View.VISIBLE
                _vacancy?.isFavorite?.isFavorite = true
                binding.buttonDeleteFromFavorites.setOnClickListener {
                    viewModel.clickOnButton()
                }
            }
        }
        binding.buttonShare.setOnClickListener {
            viewModel.shareVacancy()
        }
    }

    private fun render(stateLiveData: VacancyState) {
        when (stateLiveData) {
            is VacancyState.Loading -> loading()
            is VacancyState.Content -> content(stateLiveData.vacancy)
            is VacancyState.Error -> connectionError()
            is VacancyState.EmptyScreen -> defaultSearch()
        }
    }

    private fun initViews(vacancy: DetailVacancy) {
        _vacancy = vacancy
        with(binding) {
            job.text = vacancy.name
            salary.text = ConvertSalary().formatSalaryWithCurrency(
                vacancy.salaryFrom.toString(), vacancy.salaryTo.toString(), vacancy.salaryCurrency
            )
            Glide.with(requireContext()).load(vacancy.areaUrl).placeholder(R.drawable.ic_toast).fitCenter()
                .transform(RoundedCorners(requireContext().resources.getDimensionPixelSize(R.dimen.margin_8)))
                .into(logo)
            company.text = vacancy.employerName
            city.text = vacancy.areaName
            conditions.text = vacancy.scheduleName
            if (vacancy.experienceName == null) {
                experienceRequired.visibility = View.GONE
                experienceYears.visibility = View.GONE
            } else {
                experienceRequired.visibility = View.VISIBLE
                experienceYears.visibility = View.VISIBLE
                experienceYears.text = vacancy.experienceName
            }
            createDescription(vacancy.description)
            vacancy.keySkillsNames?.let { createSkills(it) }
            createSkills(vacancy.keySkillsNames!!)
            createContacts(vacancy)
            if (vacancy.isFavorite.isFavorite) {
                binding.buttonAddToFavorites.visibility = View.GONE
                binding.buttonDeleteFromFavorites.visibility = View.VISIBLE
            }
        }
    }

    fun createContacts(vacancy: DetailVacancy) {
        with(binding) {
            if (vacancy.contactsName != null) {
                contactPersonDescription.text = vacancy.contactsName
            } else {
                contactPerson.visibility = View.GONE
                contactPersonDescription.visibility = View.GONE
            }
            if (vacancy.contactsEmail != null) {
                emailDescription.text = vacancy.contactsEmail
                emailDescription.setOnClickListener {
                    Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:" + "${vacancy.contactsEmail}")
                    }
                }
            } else {
                email.visibility = View.GONE
                emailDescription.visibility = View.GONE
            }
            if (vacancy.contactsPhones != null) {
                var phones = ""
                vacancy.contactsPhones.forEach { phone ->
                    phones += " ${phone}\n"
                }
                phoneDescription.text = phones
                phoneDescription.setOnClickListener {
                    Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:" + "$phones")
                    }
                }
            } else {
                phoneDescription.visibility = View.GONE
                phone.visibility = View.GONE
            }
            if (vacancy.contactsName.isNullOrEmpty()
                and vacancy.contactsEmail.isNullOrEmpty() and vacancy.contactsPhones.isNullOrEmpty()) {
                contactInformation.visibility = View.GONE
            }
        }
    }

    private fun createDescription(description: String?) {
        binding.tvDescription.text = HtmlCompat.fromHtml(
            description?.replace(Regex("<li>\\s<p>|<li>"), "<li>\u00A0") ?: "",
            HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM
        )
    }

    private fun createSkills(skills: List<String?>) {
        with(binding) {
            if (skills.isEmpty()) {
                skillsRecyclerView.visibility = View.GONE
                binding.skills.visibility = View.GONE
            } else {
                var skills = ""
                skills.forEach { skill ->
                    skills += "• ${skill}\n"
                }
                skillsRecyclerView.text = skills
            }
        }
    }

    private fun loading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.fragmentNotifications.visibility = View.GONE
    }

    private fun content(data: DetailVacancy) {
        binding.progressBar.visibility = View.GONE
        initViews(data)
        binding.fragmentNotifications.visibility = View.VISIBLE
    }

    private fun defaultSearch() {
        binding.progressBar.visibility = View.GONE
        binding.fragmentNotifications.visibility = View.GONE
    }

    private fun connectionError() {
        with(binding) {
            progressBar.visibility = View.GONE
            fragmentNotifications.visibility = View.GONE
            tvServerError.visibility = View.VISIBLE
            ivServerError.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARGS_VACANCY = "vacancyId"
        fun createArgs(vacancyId: String): Bundle = bundleOf(ARGS_VACANCY to vacancyId)
    }
}
