package com.example.aqm.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.aqm.databinding.FragmentCalculateBinding

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
        val shapes = listOf("Selecciona una forma", "Rectangular", "Circular", "Triangular")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, shapes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerShape.adapter = adapter

        binding.spinnerShape.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    1 -> showForm("Rectangular")
                    2 -> showForm("Circular")
                    3 -> showForm("Triangular")
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
                "Rectangular" -> {
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
                "Circular" -> {
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
                "Triangular" -> {
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
            calculateChemicals()
        }
    }

    private fun showForm(shape: String) {
        binding.formGroup.visibility = View.VISIBLE
        binding.formRectangular.visibility = if (shape == "Rectangular") View.VISIBLE else View.GONE
        binding.formCircular.visibility = if (shape == "Circular") View.VISIBLE else View.GONE
        binding.formTriangular.visibility = if (shape == "Triangular") View.VISIBLE else View.GONE
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
            apply() // Guardar los cambios
        }

        // Mostrar un mensaje de éxito
        Toast.makeText(requireContext(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show()

        // Actualizar la interfaz de usuario con los nuevos datos
        loadPoolData()
    }

    private fun showError() {
        Toast.makeText(requireContext(), "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun calculateChemicals() {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        val volume = sharedPrefs.getFloat("volume", 0f)

        if (volume > 0) {
            // Ejemplo de cálculos de productos químicos
            val chlorine = volume * 0.00013f // 130g de cloro por cada 1000 litros
            val algaecide = volume * 0.00002f // 20ml de alguicida por cada 1000 litros
            val phMinus = volume * 0.0001f // 100g de pH minus por cada 1000 litros

            val message = """
                Productos Químicos Necesarios:
                - Cloro: ${chlorine}kg
                - Alguicida: ${algaecide}L
                - pH Minus: ${phMinus}kg
            """.trimIndent()

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "No se ha encontrado el volumen de la piscina.", Toast.LENGTH_SHORT).show()
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

            binding.tvSavedShape.text = "Forma: $shape"
            binding.tvSavedDimensions.text = "Dimensiones: $length x $width x $depth m"
            binding.tvSavedFrequency.text = "Frecuencia: cada $frequency días"
            binding.tvSavedVolume.text = "Volumen: $volume m³"
        } else {
            // Mostrar formulario para ingresar datos
            binding.textViewNoData.visibility = View.VISIBLE
            binding.formGroup.visibility = View.GONE
            binding.savedDataGroup.visibility = View.GONE
        }
    }
}