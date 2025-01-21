package com.example.aqm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aqm.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Configuración del view binding
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el Spinner para cambiar idioma
        val languages = listOf("Español", "English")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        // Detectar cuando el idioma cambia realmente
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            var isFirstSelection = true // Evitar mostrar el mensaje al abrir la pantalla
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!isFirstSelection) {
                    val selectedLanguage = parent.getItemAtPosition(position).toString()
                    Toast.makeText(requireContext(), "Idioma cambiado a $selectedLanguage", Toast.LENGTH_SHORT).show()
                }
                isFirstSelection = false
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada si no se selecciona nada
            }
        }

        // Configurar acciones para botones adicionales
        binding.buttonViewHistory.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo historial de limpiezas...", Toast.LENGTH_SHORT).show()
        }

        binding.buttonResetPoolData.setOnClickListener {
            Toast.makeText(requireContext(), "Datos de piscina reestablecidos.", Toast.LENGTH_SHORT).show()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) {
                "Notificaciones habilitadas"
            } else {
                "Notificaciones deshabilitadas"
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }
}
