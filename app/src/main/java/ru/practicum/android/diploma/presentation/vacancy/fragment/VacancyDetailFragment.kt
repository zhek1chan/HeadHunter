package ru.practicum.android.diploma.presentation.vacancy.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import ru.practicum.android.diploma.presentation.search.fragment.gone
import ru.practicum.android.diploma.presentation.search.fragment.visible
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

        if (viewModel.checkBeforeRender(vacancyId!!)) {
            viewModel.getVacancyFromDb(vacancyId!!)
            checkButton(true)
        } else {
            viewModel.getVacancyDetail(vacancyId!!)
            checkButton(false)
        }

        viewModel.vacancyState.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        binding.buttonShare.setOnClickListener {
            viewModel.shareVacancy()
        }

        if (vacancyId == null) {
            onDestroy()
        }
    }

    private fun render(stateLiveData: VacancyState) {
        when (stateLiveData) {
            is VacancyState.Loading -> loading()
            is VacancyState.Content -> content(stateLiveData.vacancy)
            is VacancyState.Error -> connectionError()
            is VacancyState.EmptyScreen -> defaultSearch()
            is VacancyState.ContentFromDb -> content(stateLiveData.vacancy)
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
            createSkills(vacancy)
            createContacts(vacancy)
            if (vacancy.isFavorite.isFavorite) {
                binding.buttonAddToFavorites.visibility = View.GONE
                binding.buttonDeleteFromFavorites.visibility = View.VISIBLE
            }
            if (vacancy.comment.isNullOrEmpty()) {
                comment.gone()
                commentDescription.gone()
            } else {
                comment.visible()
                commentDescription.visible()
                commentDescription.text = vacancy.comment
            }
        }
    }

    private fun createSkills(vacancy: DetailVacancy) {
        with(binding) {
            if (vacancy.keySkillsNames != null && vacancy.keySkillsNames.isNotEmpty()) {
                var formattedSkills = vacancy.keySkillsNames.joinToString(prefix = "• ") {
                    it.replace(",", "\n•")
                }
                formattedSkills = formattedSkills.filter { char -> char != '[' && char != ']' }
                skillsRecyclerView.text = formattedSkills
                skillsRecyclerView.visibility = View.VISIBLE
                skills.visibility = View.VISIBLE
            } else {
                skillsRecyclerView.visibility = View.GONE
                skills.visibility = View.GONE
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
                vacancy.contactsPhones.forEach { phone -> phones += " ${phone}\n"
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
            vacancyToolbars.visibility = View.VISIBLE
            serverErrorLayout.visibility = View.VISIBLE
            vacancyConstraintLayout.visibility = View.GONE
        }
    }

    private fun checkButton(fromDb: Boolean) {
        viewModel.onLikedCheck(vacancyId!!).observe(requireActivity()) { likeIndicator ->
            when (likeIndicator) {
                true -> {
                    binding.buttonAddToFavorites.visibility = View.VISIBLE
                    binding.buttonDeleteFromFavorites.visibility = View.GONE
                    _vacancy?.isFavorite?.isFavorite = false
                    binding.buttonAddToFavorites.setOnClickListener {
                        Log.d("FragmentVacancy", "Press on like :)")
                        if (fromDb) {
                            viewModel.clickOnLikeWithDb()
                        } else {
                            viewModel.clickOnLike()
                        }
                    }
                }

                false -> {
                    binding.buttonAddToFavorites.visibility = View.GONE
                    binding.buttonDeleteFromFavorites.visibility = View.VISIBLE
                    _vacancy?.isFavorite?.isFavorite = true
                    binding.buttonDeleteFromFavorites.setOnClickListener {
                        Log.d("FragmentVacancy", "Press on dislike :(")
                        if (fromDb) {
                            viewModel.clickOnLikeWithDb()
                        } else {
                            viewModel.clickOnLike()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ARGS_VACANCY = "vacancyId"
    }
}
