package com.example.aqm.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aqm.MainActivity
import com.example.aqm.R
import com.example.aqm.databinding.FragmentSettingsBinding
import java.util.Locale

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val savedLanguage = sharedPrefs.getString("language", Locale.getDefault().language) ?: "es"

        // Configurar el Spinner con los idiomas
        val languages = listOf(getString(R.string.language_spanish), getString(R.string.language_english))
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerLanguage.adapter = adapter

        // Seleccionar el idioma guardado en el Spinner
        binding.spinnerLanguage.setSelection(if (savedLanguage == "en") 1 else 0)

        // Detectar cuando el usuario selecciona otro idioma
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val newLanguage = if (position == 1) "en" else "es"
                if (newLanguage != savedLanguage) {
                    changeAppLanguage(newLanguage)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun changeAppLanguage(languageCode: String) {
        // Guardar el nuevo idioma en SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("language", languageCode).apply()

        // Aplicar configuración del nuevo idioma
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)

        // Actualizar el contexto de la aplicación
        requireActivity().baseContext.resources.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)

        // Reiniciar la actividad principal
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
