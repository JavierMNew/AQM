package com.example.aqm.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)

        // Verificar si ya hay datos guardados
        val shape = sharedPrefs.getString("shape", null)
        val length = sharedPrefs.getFloat("length", 0f)
        val width = sharedPrefs.getFloat("width", 0f)
        val depth = sharedPrefs.getFloat("depth", 0f)
        val frequency = sharedPrefs.getInt("frequency", 0)

        if (shape != null && length > 0 && width > 0 && depth > 0) {
            // Si los datos ya están guardados, mostrarlos en lugar del formulario
            binding.textViewNoData.visibility = View.GONE
            binding.formGroup.visibility = View.GONE
            binding.savedDataGroup.visibility = View.VISIBLE

            binding.tvSavedShape.text = "Forma: $shape"
            binding.tvSavedDimensions.text = "Dimensiones: $length x $width x $depth m"
            binding.tvSavedFrequency.text = "Frecuencia: cada $frequency días"
        } else {
            // Mostrar formulario para ingresar datos
            binding.textViewNoData.visibility = View.VISIBLE
            binding.formGroup.visibility = View.VISIBLE
            binding.savedDataGroup.visibility = View.GONE
        }

        // Configurar el botón de guardar datos
        binding.btnSave.setOnClickListener {
            val newShape = binding.etShape.text.toString()
            val newLength = binding.etLength.text.toString().toFloatOrNull()
            val newWidth = binding.etWidth.text.toString().toFloatOrNull()
            val newDepth = binding.etDepth.text.toString().toFloatOrNull()
            val newFrequency = binding.etFrecuency.text.toString().toIntOrNull()

            if (!newShape.isNullOrBlank() && newLength != null && newWidth != null && newDepth != null && newFrequency != null) {
                // Guardar los datos en SharedPreferences
                sharedPrefs.edit().apply {
                    putString("shape", newShape)
                    putFloat("length", newLength)
                    putFloat("width", newWidth)
                    putFloat("depth", newDepth)
                    putInt("frequency", newFrequency)
                    apply() // Guardar los cambios
                }

                // Mostrar un mensaje de éxito
                Toast.makeText(requireContext(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show()

                // Refrescar el fragmento para mostrar los datos guardados
                refreshFragment()
            } else {
                // Mostrar un mensaje de error si algún campo está vacío o es inválido
                Toast.makeText(requireContext(), "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshFragment() {
        // Recargar el fragmento para mostrar los datos actualizados
        requireActivity().supportFragmentManager.beginTransaction().apply {
            detach(this@CalculateFragment)
            attach(this@CalculateFragment)
            commit()
        }
    }

}
