package com.example.aqm.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d("HomeFragment", "onAttach called")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragment", "onCreate called")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("HomeFragment", "onCreateView called")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("HomeFragment", "onViewCreated called")
        sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        loadPoolData()
        updateCurrentDate()

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, 0)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("HomeFragment", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("HomeFragment", "onResume called")
        // Registrar el listener
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
        // Cargar los datos cada vez que el fragmento se reanuda
        loadPoolData()
    }

    override fun onPause() {
        super.onPause()
        Log.d("HomeFragment", "onPause called")
        // Desregistrar el listener
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onStop() {
        super.onStop()
        Log.d("HomeFragment", "onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("HomeFragment", "onDestroyView called")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("HomeFragment", "onDestroy called")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d("HomeFragment", "onDetach called")
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
        } else {
            // Mostrar mensaje de que no hay datos
            binding.tvPoolVolume.text = getString(R.string.no_pool_data)
            binding.tvDaysSinceLastCleaning.text = getString(R.string.days_since_last_cleaning, 0)
        }
    }

    private fun updateCurrentDate() {
        val currentDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
        binding.tvCurrentDate.text = getString(R.string.current_date, currentDate)
    }
}