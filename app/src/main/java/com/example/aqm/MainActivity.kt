package com.example.aqm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aqm.fragments.HomeFragment
import com.example.aqm.fragments.CalculateFragment
import com.example.aqm.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // Variables para almacenar las instancias de los fragmentos
    private lateinit var homeFragment: HomeFragment
    private lateinit var calculateFragment: CalculateFragment
    private lateinit var settingsFragment: SettingsFragment
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar los fragmentos
        homeFragment = HomeFragment()
        calculateFragment = CalculateFragment()
        settingsFragment = SettingsFragment()

        // Cargar HomeFragment por defecto
        activeFragment = homeFragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, homeFragment, "HomeFragment")
            .add(R.id.fragmentContainer, calculateFragment, "CalculateFragment")
            .add(R.id.fragmentContainer, settingsFragment, "SettingsFragment")
            .hide(calculateFragment)
            .hide(settingsFragment)
            .commit()

        // Configurar la navegación inferior
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> switchFragment(homeFragment, "HomeFragment")
                R.id.calculate -> switchFragment(calculateFragment, "CalculateFragment")
                R.id.settings -> switchFragment(settingsFragment, "SettingsFragment")
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment, tag: String) {
        if (fragment == activeFragment) return // Evitar cambiar al mismo fragmento

        supportFragmentManager.beginTransaction().apply {
            hide(activeFragment!!) // Ocultar el fragmento activo actual
            show(fragment) // Mostrar el nuevo fragmento
            activeFragment = fragment // Actualizar el fragmento activo
            commit()
        }
    }
}