package com.example.aqm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.aqm.R
import com.example.aqm.databinding.FragmentCalculateBinding
import java.util.Locale

class CalculateFragment : Fragment() {

    private var _binding: FragmentCalculateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ajustar margen superior para respetar el notch
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBarsInsets.top, 0, 0)
            insets
        }

        // Configurar botón para agregar datos de la piscina
        binding.buttonAddPoolData.setOnClickListener {
            startPoolDataSetup()
        }
    }

    private fun startPoolDataSetup() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_pool_data_setup, null)
        builder.setView(dialogView)

        val shapeSpinner = dialogView.findViewById<Spinner>(R.id.spinnerPoolShape)
        val lengthInput = dialogView.findViewById<EditText>(R.id.editTextLength)
        val widthInput = dialogView.findViewById<EditText>(R.id.editTextWidth)
        val depthInput = dialogView.findViewById<EditText>(R.id.editTextDepth)
        val frequencySpinner = dialogView.findViewById<Spinner>(R.id.spinnerFrequency)

        // Configurar adaptadores para los spinners
        shapeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.pool_shapes)
        )

        frequencySpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(R.array.cleaning_frequencies)
        )

        builder.setTitle("Configurar Datos de la Piscina")
        builder.setPositiveButton("Guardar") { _, _ ->
            val shape = shapeSpinner.selectedItem.toString()
            val length = lengthInput.text.toString().toDoubleOrNull()
            val width = widthInput.text.toString().toDoubleOrNull()
            val depth = depthInput.text.toString().toDoubleOrNull()
            val frequency = frequencySpinner.selectedItem.toString()

            if (length == null || width == null || depth == null) {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            } else {
                val volume = calculatePoolVolume(shape, length, width, depth)
                val chlorineNeeded = calculateChlorine(volume)
                Toast.makeText(
                    requireContext(),
                    "Volumen: $volume L\nCloro requerido: $chlorineNeeded g\nFrecuencia: $frequency",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        builder.setNegativeButton("Cancelar", null)
        builder.create().show()
    }

    private fun calculatePoolVolume(shape: String, length: Double, width: Double, depth: Double): Double {
        return when (shape.lowercase(Locale.getDefault())) {
            "rectangular" -> length * width * depth * 1000 // Convertir a litros
            "circular" -> Math.PI * Math.pow(length / 2, 2.0) * depth * 1000 // Largo como diámetro
            else -> 0.0
        }
    }

    private fun calculateChlorine(volume: Double): Double {
        return volume * 0.00025 // Ejemplo: 0.25 g por litro
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
