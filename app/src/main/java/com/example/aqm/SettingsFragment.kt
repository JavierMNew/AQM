package com.example.aqm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aqm.R

class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val btnChangeLanguage = view.findViewById<Button>(R.id.btnChangeLanguage)
        val btnResetData = view.findViewById<Button>(R.id.btnResetData)

        btnChangeLanguage.setOnClickListener {
            Toast.makeText(context, "Función de cambio de idioma no implementada aún.", Toast.LENGTH_SHORT).show()
        }

        btnResetData.setOnClickListener {
            Toast.makeText(context, "Función para restablecer datos no implementada aún.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}