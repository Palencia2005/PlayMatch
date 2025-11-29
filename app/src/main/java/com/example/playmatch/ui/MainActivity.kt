package com.example.playmatch.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.playmatch.R

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_main)
            
            try {
                val navController = findNavController(R.id.nav_host_fragment)
                setupActionBarWithNavController(navController)
            } catch (e: Exception) {
                Log.e(TAG, "Error al configurar navegación", e)
                // Continuar sin la barra de acción si hay un error
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error al inicializar MainActivity", e)
            e.printStackTrace()
            // Intentar mostrar un layout simple si hay un error crítico
            try {
                setContentView(android.R.layout.simple_list_item_1)
            } catch (e2: Exception) {
                Log.e(TAG, "Error crítico, cerrando app", e2)
                finish()
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return try {
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigateUp() || super.onSupportNavigateUp()
        } catch (e: Exception) {
            Log.e(TAG, "Error en navegación", e)
            super.onSupportNavigateUp()
        }
    }
}

