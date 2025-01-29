package com.example.aqm.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aqm.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPoolData()
    }

    override fun onResume() {
        super.onResume()
        // Cargar los datos cada vez que el fragmento se reanuda
        loadPoolData()
    }

    private fun loadPoolData() {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        val shape = sharedPrefs.getString("shape", "No definido")
        val length = sharedPrefs.getFloat("length", 0f)
        val width = sharedPrefs.getFloat("width", 0f)
        val depth = sharedPrefs.getFloat("depth", 0f)
        val frequency = sharedPrefs.getInt("frequency", 0)

        // Calcular el volumen (largo * ancho * profundidad * 1000 para convertir a litros)
        val volume = length * width * depth * 1000

        // Mostrar los datos en los TextView correspondientes
        binding.tvPoolVolume.text = """
            Forma de la piscina: $shape
            Largo: ${length}m
            Ancho: ${width}m
            Profundidad: ${depth}m
            Volumen: ${volume}L
            Frecuencia de limpieza: cada $frequency días
        """.trimIndent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}