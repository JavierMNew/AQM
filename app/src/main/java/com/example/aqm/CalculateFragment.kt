package com.example.aqm.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.aqm.MainActivity // Importar MainActivity
import com.example.aqm.R
import com.example.aqm.databinding.FragmentCalculateBinding
import java.text.SimpleDateFormat
import java.util.*

class CalculateFragment : Fragment() {

    private lateinit var binding: FragmentCalculateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalculateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ajustar márgenes para notch y bordes
        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemInsets.left, systemInsets.top, systemInsets.right, 0)
            insets
        }

        // Configurar el Spinner para seleccionar la forma de la piscina
        val shapes = listOf(
            getString(R.string.select_shape),
            getString(R.string.shape_rectangular),
            getString(R.string.shape_circular),
            getString(R.string.shape_triangular)
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, shapes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerShape.adapter = adapter

        binding.spinnerShape.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    1 -> showForm(getString(R.string.shape_rectangular))
                    2 -> showForm(getString(R.string.shape_circular))
                    3 -> showForm(getString(R.string.shape_triangular))
                    else -> hideForms()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                hideForms()
            }
        }

        loadPoolData()

        // Configurar el botón de guardar datos
        binding.btnSave.setOnClickListener {
            val shape = binding.spinnerShape.selectedItem.toString()
            val frequency = binding.etFrecuency.text.toString().toIntOrNull()

            when (shape) {
                getString(R.string.shape_rectangular) -> {
                    val length = binding.etLength.text.toString().toFloatOrNull()
                    val width = binding.etWidth.text.toString().toFloatOrNull()
                    val depth = binding.etDepth.text.toString().toFloatOrNull()

                    if (length != null && width != null && depth != null && frequency != null) {
                        val volume = length * width * depth
                        savePoolData(shape, length, width, depth, frequency, volume.toFloat())
                    } else {
                        showError()
                    }
                }
                getString(R.string.shape_circular) -> {
                    val diameter = binding.etDiameter.text.toString().toFloatOrNull()
                    val depth = binding.etDepthCircular.text.toString().toFloatOrNull()

                    if (diameter != null && depth != null && frequency != null) {
                        val coefficient = 0.79
                        val volume = diameter * diameter * depth * coefficient
                        savePoolData(shape, diameter, diameter, depth, frequency, volume.toFloat())
                    } else {
                        showError()
                    }
                }
                getString(R.string.shape_triangular) -> {
                    val base = binding.etBase.text.toString().toFloatOrNull()
                    val height = binding.etHeight.text.toString().toFloatOrNull()
                    val depth = binding.etDepthTriangular.text.toString().toFloatOrNull()

                    if (base != null && height != null && depth != null && frequency != null) {
                        val volume = 0.5 * base * height * depth
                        savePoolData(shape, base, height, depth, frequency, volume.toFloat())
                    } else {
                        showError()
                    }
                }
                else -> showError()
            }
        }

        // Configurar el botón de cálculo de productos químicos
        binding.btnCalculateChemicals.setOnClickListener {
            showChemicalOptionsDialog()
        }

        // Configurar el botón de agregar cloro
        binding.btnAddChlorine.setOnClickListener {
            addChlorine()
        }
    }

    private fun showForm(shape: String) {
        binding.formGroup.visibility = View.VISIBLE
        binding.formRectangular.visibility = if (shape == getString(R.string.shape_rectangular)) View.VISIBLE else View.GONE
        binding.formCircular.visibility = if (shape == getString(R.string.shape_circular)) View.VISIBLE else View.GONE
        binding.formTriangular.visibility = if (shape == getString(R.string.shape_triangular)) View.VISIBLE else View.GONE
    }

    private fun hideForms() {
        binding.formGroup.visibility = View.GONE
        binding.formRectangular.visibility = View.GONE
        binding.formCircular.visibility = View.GONE
        binding.formTriangular.visibility = View.GONE
    }

    private fun savePoolData(shape: String, length: Float, width: Float, depth: Float, frequency: Int, volume: Float) {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        sharedPrefs.edit().apply {
            putString("shape", shape)
            putFloat("length", length)
            putFloat("width", width)
            putFloat("depth", depth)
            putInt("frequency", frequency)
            putFloat("volume", volume)
            putBoolean("chlorine_added", false) // Reset chlorine added status
            apply() // Guardar los cambios
        }

        // Mostrar un mensaje de éxito
        Toast.makeText(requireContext(), getString(R.string.data_saved), Toast.LENGTH_SHORT).show()

        // Actualizar la interfaz de usuario con los nuevos datos
        loadPoolData()
    }

    private fun showError() {
        Toast.makeText(requireContext(), getString(R.string.error_complete_fields), Toast.LENGTH_SHORT).show()
    }

    private fun showChemicalOptionsDialog() {
        val options = arrayOf(getString(R.string.calculate_chlorine))
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.calculate_chemicals))
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> calculateChlorine()
            }
        }
        builder.show()
    }

    private fun calculateChlorine() {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        val volume = sharedPrefs.getFloat("volume", 0f)

        if (volume > 0) {
            val chlorine = volume * 3 / 1000 // 3 gramos por cada 1,000 litros
            binding.tvChlorineAmount.text = getString(R.string.chlorine_needed, chlorine)
            val chlorineAdded = sharedPrefs.getBoolean("chlorine_added", false)
            if (chlorineAdded) {
                binding.btnAddChlorine.visibility = View.GONE
                binding.tvChlorineAmount.text = "${binding.tvChlorineAmount.text} (${getString(R.string.chlorine_added)})"
            } else {
                binding.btnAddChlorine.visibility = View.VISIBLE
            }
            binding.calculationResultsGroup.visibility = View.VISIBLE
        } else {
            Toast.makeText(requireContext(), getString(R.string.no_pool_volume), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addChlorine() {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        editor.putString("last_cleaning_date", currentDate)
        editor.putBoolean("chlorine_added", true)
        editor.apply()

        Toast.makeText(requireContext(), getString(R.string.chlorine_added), Toast.LENGTH_SHORT).show()
        binding.btnAddChlorine.visibility = View.GONE
        binding.tvChlorineAmount.text = "${binding.tvChlorineAmount.text} (${getString(R.string.chlorine_added)})"
        updateNextCleaningDate()

        // Notificar a HomeFragment que se ha agregado cloro
        (requireActivity() as MainActivity).notifyChlorineAdded()
    }

    private fun updateNextCleaningDate() {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        val frequency = sharedPrefs.getInt("frequency", 0)
        val lastCleaningDateStr = sharedPrefs.getString("last_cleaning_date", null)

        if (lastCleaningDateStr != null && frequency > 0) {
            val lastCleaningDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(lastCleaningDateStr)
            val nextCleaningDate = Calendar.getInstance().apply {
                time = lastCleaningDate!!
                add(Calendar.DAY_OF_YEAR, frequency)
            }.time

            val daysUntilNextCleaning = ((nextCleaningDate.time - Date().time) / (1000 * 60 * 60 * 24)).toInt()
            binding.tvNextCleaning.text = getString(R.string.next_cleaning_in_days, daysUntilNextCleaning)
        } else {
            binding.tvNextCleaning.text = getString(R.string.next_cleaning_in_days, 0)
        }
    }

    fun loadPoolData() {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        val shape = sharedPrefs.getString("shape", null)
        val length = sharedPrefs.getFloat("length", 0f)
        val width = sharedPrefs.getFloat("width", 0f)
        val depth = sharedPrefs.getFloat("depth", 0f)
        val frequency = sharedPrefs.getInt("frequency", 0)
        val volume = sharedPrefs.getFloat("volume", 0f)

        if (shape != null && length > 0 && width > 0 && depth > 0) {
            // Si los datos ya están guardados, mostrarlos en lugar del formulario
            binding.textViewNoData.visibility = View.GONE
            binding.formGroup.visibility = View.GONE
            binding.savedDataGroup.visibility = View.VISIBLE

            binding.tvSavedShape.text = getString(R.string.pool_shape, shape)
            binding.tvSavedDimensions.text = getString(R.string.pool_length, length) + " x " + getString(R.string.pool_width, width) + " x " + getString(R.string.pool_depth, depth)
            binding.tvSavedFrequency.text = getString(R.string.pool_frequency, frequency)
            binding.tvSavedVolume.text = getString(R.string.pool_volume, volume)

            updateNextCleaningDate()
        } else {
            // Mostrar formulario para ingresar datos
            binding.textViewNoData.visibility = View.VISIBLE
            binding.formGroup.visibility = View.GONE
            binding.savedDataGroup.visibility = View.GONE
        }
    }

    fun resetPoolData() {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()

        // Ocultar los datos guardados y el cálculo de cloro
        binding.savedDataGroup.visibility = View.GONE
        binding.calculationResultsGroup.visibility = View.GONE
        binding.textViewNoData.visibility = View.VISIBLE

        Toast.makeText(requireContext(), getString(R.string.pool_data_reset), Toast.LENGTH_SHORT).show()
    }
}