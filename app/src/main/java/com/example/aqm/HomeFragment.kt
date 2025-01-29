package com.example.aqm.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aqm.databinding.FragmentHomeBinding

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

        if (shape != null && length > 0 && width > 0 && depth > 0) {
            // Mostrar datos de la piscina
            val volume = length * width * depth * 1000 // Calcular volumen en litros
            binding.tvPoolVolume.text = """
            Forma de la piscina: $shape
            Largo: ${length}m
            Ancho: ${width}m
            Profundidad: ${depth}m
            Volumen: ${volume}L
            Frecuencia de limpieza: cada $frequency días
        """.trimIndent()
        } else {
            // Mostrar mensaje de que no hay datos
            binding.tvPoolVolume.text = "No hay datos registrados para la piscina."
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}