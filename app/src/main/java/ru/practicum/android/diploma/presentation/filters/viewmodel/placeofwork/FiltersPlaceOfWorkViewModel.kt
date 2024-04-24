import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.practicum.android.diploma.domain.models.Area
import ru.practicum.android.diploma.domain.models.Country

class FiltersPlaceOfWorkViewModel : ViewModel() {
    private val _selectedCountry = MutableLiveData<Country?>()
    val selectedCountry: LiveData<Country?> get() = _selectedCountry

    private val _selectedRegion = MutableLiveData<Area?>()
    val selectedRegion: LiveData<Area?> get() = _selectedRegion

    fun setSelectedCountry(country: Country?) {
        _selectedCountry.value = country
    }

    fun setSelectedRegion(region: Area?) {
        _selectedRegion.value = region
    }

    fun clearSelectedFilters() {
        _selectedCountry.value = null
        _selectedRegion.value = null
    }
}
