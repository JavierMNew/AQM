package com.example.aqm.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aqm.MainActivity
import com.example.aqm.databinding.FragmentSettingsBinding
import java.util.Locale

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
                    changeAppLanguage(selectedLanguage)
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
            // Navegar a una nueva actividad o fragmento para mostrar el historial
            Toast.makeText(requireContext(), "Abriendo historial de limpiezas...", Toast.LENGTH_SHORT).show()
        }

        binding.buttonResetPoolData.setOnClickListener {
            resetPoolData()
            Toast.makeText(requireContext(), "Datos de piscina reestablecidos.", Toast.LENGTH_SHORT).show()
        }

        // Cargar el estado actual de las notificaciones
        val sharedPrefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        binding.switchNotifications.isChecked = sharedPrefs.getBoolean("notifications_enabled", true)

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val message = if (isChecked) {
                "Notificaciones habilitadas"
            } else {
                "Notificaciones deshabilitadas"
            }
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

            // Guardar el estado de las notificaciones
            sharedPrefs.edit().putBoolean("notifications_enabled", isChecked).apply()
        }
    }

    private fun changeAppLanguage(language: String) {
        val languageCode = when (language) {
            "English" -> "en"
            else -> "es" // Español por defecto
        }

        // Guardar el idioma seleccionado en SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("app_language", languageCode).apply()

        // Reiniciar la actividad para aplicar el cambio de idioma
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun resetPoolData() {
        val sharedPrefs = requireContext().getSharedPreferences("pool_data", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply() // Borrar todos los datos

        // Notificar a los fragmentos que los datos han cambiado
        (requireActivity() as MainActivity).refreshFragments()

        Toast.makeText(requireContext(), "Datos de piscina reestablecidos.", Toast.LENGTH_SHORT).show()
    }
}