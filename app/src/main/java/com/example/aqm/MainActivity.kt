package com.example.aqm

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.aqm.fragments.HomeFragment
import com.example.aqm.fragments.CalculateFragment
import com.example.aqm.fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Variables para almacenar las instancias de los fragmentos
    private lateinit var homeFragment: HomeFragment
    private lateinit var calculateFragment: CalculateFragment
    private lateinit var settingsFragment: SettingsFragment
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")
        setDefaultLanguage() // Establecer el idioma por defecto al iniciar
        setContentView(R.layout.activity_main)

        // Inicializar los fragmentos
        homeFragment = HomeFragment()
        calculateFragment = CalculateFragment()
        settingsFragment = SettingsFragment()

        if (savedInstanceState == null) {
            // Cargar HomeFragment por defecto
            activeFragment = homeFragment
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, homeFragment, "HomeFragment")
                .add(R.id.fragmentContainer, calculateFragment, "CalculateFragment")
                .add(R.id.fragmentContainer, settingsFragment, "SettingsFragment")
                .hide(calculateFragment)
                .hide(settingsFragment)
                .commit()
        } else {
            // Restaurar el fragmento activo
            homeFragment = supportFragmentManager.findFragmentByTag("HomeFragment") as HomeFragment
            calculateFragment = supportFragmentManager.findFragmentByTag("CalculateFragment") as CalculateFragment
            settingsFragment = supportFragmentManager.findFragmentByTag("SettingsFragment") as SettingsFragment
            activeFragment = supportFragmentManager.getFragment(savedInstanceState, "activeFragment")
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Guardar el fragmento activo
        supportFragmentManager.putFragment(outState, "activeFragment", activeFragment!!)
    }

    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
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

    private fun setDefaultLanguage() {
        val sharedPrefs = getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val language = sharedPrefs.getString("app_language", "es") ?: "es" // Español por defecto
        setAppLanguage(language)
    }

    private fun setAppLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun refreshFragments() {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is CalculateFragment) {
                fragment.resetPoolData()
            }
        }
    }

    fun notifyChlorineAdded() {
        val fragments = supportFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is HomeFragment) {
                fragment.loadPoolData()
            }
        }
    }
}