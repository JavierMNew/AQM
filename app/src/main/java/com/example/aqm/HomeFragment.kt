package com.example.aqm.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aqm.R
import com.example.aqm.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPrefs: SharedPreferences
    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
        loadPoolData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        loadPoolData()
        updateCurrentDate()

        // Configurar el botón de agregar cloro
        binding.btnAddChlorineHome.setOnClickListener {
            addChlorine()
        }
    }

    override fun onResume() {
        super.onResume()
        // Registrar el listener
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        // Cargar los datos cada vez que el fragmento se reanuda
        loadPoolData()
    }

    override fun onPause() {
        super.onPause()
        // Desregistrar el listener
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    fun loadPoolData() {
        val shape = sharedPrefs.getString("shape", null)
        val length = sharedPrefs.getFloat("length", 0f)
        val width = sharedPrefs.getFloat("width", 0f)
        val depth = sharedPrefs.getFloat("depth", 0f)
        val frequency = sharedPrefs.getInt("frequency", 0)
        val volume = sharedPrefs.getFloat("volume", 0f)
        val lastCleaningDateStr = sharedPrefs.getString("last_cleaning_date", null)
        val chlorineAdded = sharedPrefs.getBoolean("chlorine_added", false)

        if (shape != null && length > 0 && width > 0 && depth > 0) {
            // Mostrar datos de la piscina
            binding.tvPoolVolume.text = getString(R.string.pool_volume, volume)
            binding.tvPoolStatus.text = getString(R.string.pool_status)
            binding.tvPoolVolume.text = """
                ${getString(R.string.pool_shape, shape)}
                ${getString(R.string.pool_length, length)}
                ${getString(R.string.pool_width, width)}
                ${getString(R.string.pool_depth, depth)}
                ${getString(R.string.pool_volume, volume)}
                ${getString(R.string.pool_frequency, frequency)}
            """.trimIndent()

            // Calcular días desde la última limpieza
            if (lastCleaningDateStr != null) {
                val lastCleaningDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(lastCleaningDateStr)
                val daysSinceLastCleaning = ((Date().time - lastCleaningDate!!.time) / (1000 * 60 * 60 * 24)).toInt()
                binding.tvDaysSinceLastCleaning.text = getString(R.string.days_since_last_cleaning, daysSinceLastCleaning)
            } else {
                binding.tvDaysSinceLastCleaning.text = getString(R.string.days_since_last_cleaning, 0)
            }

            // Mostrar u ocultar el botón de agregar cloro
            if (chlorineAdded) {
                binding.btnAddChlorineHome.visibility = View.GONE
                binding.tvChlorineStatus.text = getString(R.string.chlorine_added)
            } else {
                binding.btnAddChlorineHome.visibility = View.VISIBLE
                binding.tvChlorineStatus.text = ""
            }
        } else {
            // Mostrar mensaje de que no hay datos
            binding.tvPoolVolume.text = getString(R.string.no_pool_data)
            binding.tvDaysSinceLastCleaning.text = getString(R.string.days_since_last_cleaning, 0)
            binding.btnAddChlorineHome.visibility = View.GONE
            binding.tvChlorineStatus.text = ""
        }
    }

    private fun updateCurrentDate() {
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        binding.tvCurrentDate.text = getString(R.string.current_date, currentDate)
    }

    private fun addChlorine() {
        val editor = sharedPrefs.edit()
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        editor.putString("last_cleaning_date", currentDate)
        editor.putBoolean("chlorine_added", true)
        editor.apply()

        Toast.makeText(requireContext(), getString(R.string.chlorine_added), Toast.LENGTH_SHORT).show()
        binding.btnAddChlorineHome.visibility = View.GONE
        binding.tvChlorineStatus.text = getString(R.string.chlorine_added)
        loadPoolData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}