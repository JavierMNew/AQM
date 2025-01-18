package com.example.aqm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import com.example.aqm.R
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Configurar la fecha actual
        val currentDateTextView = view.findViewById<TextView>(R.id.tvCurrentDate)
        val sdf = SimpleDateFormat("dd 'de' MMMM 'del' yyyy", Locale("es", "ES"))
        currentDateTextView.text = "Hoy estamos a ${sdf.format(Date())}"

        // Actualizar otros datos estáticos de la piscina
        val daysSinceCleaningTextView = view.findViewById<TextView>(R.id.tvDaysSinceLastCleaning)
        daysSinceCleaningTextView.text = "Han pasado 7 días desde la última limpieza"

        return view
    }
}