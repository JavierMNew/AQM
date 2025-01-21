package com.example.aqm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aqm.R

class CalculateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculate, container, false)

        val etPoolSize = view.findViewById<EditText>(R.id.etPoolSize)
        val btnCalculate = view.findViewById<Button>(R.id.btnCalculate)
        val tvResult = view.findViewById<TextView>(R.id.tvResult)

        btnCalculate.setOnClickListener {
            val poolSizeText = etPoolSize.text.toString()
            if (poolSizeText.isNotEmpty()) {
                val poolSize = poolSizeText.toDouble()
                val cleaningProductAmount = poolSize * 0.1
                tvResult.text = "Resultado: Necesitas $cleaningProductAmount litros de producto."
            } else {
                Toast.makeText(context, "Por favor, ingresa el tamaño de la piscina.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}