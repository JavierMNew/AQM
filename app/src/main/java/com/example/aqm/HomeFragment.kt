package com.example.aqm.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.aqm.R
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var tvDateTime: TextView
    private lateinit var tvDaysSinceLastClean: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        tvDateTime = view.findViewById(R.id.tvDateTime)
        tvDaysSinceLastClean = view.findViewById(R.id.tvDaysSinceLastClean)

        updateDateTime()
        updateDaysSinceLastClean()

        return view
    }

    private fun updateDateTime() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val calendar = Calendar.getInstance()
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale("es", "ES"))
                val year = calendar.get(Calendar.YEAR)

                val formattedDate = "Hoy estamos a $day de $month del año $year"
                tvDateTime.text = formattedDate

                handler.postDelayed(this, 1000)
            }
        }, 0)
    }

    private fun updateDaysSinceLastClean() {
        // Simulación: Fecha de última limpieza
        val lastCleanDate = Calendar.getInstance().apply {
            set(2025, Calendar.JANUARY, 1) // Cambia esta fecha según sea necesario
        }
        val currentDate = Calendar.getInstance()
        val diffInMillis = currentDate.timeInMillis - lastCleanDate.timeInMillis
        val daysSinceLastClean = diffInMillis / (1000 * 60 * 60 * 24)
        tvDaysSinceLastClean.text = "Días desde la última limpieza: $daysSinceLastClean"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
    }
}