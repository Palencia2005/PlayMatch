package com.example.playmatch

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

class PlayMatchApplication : Application() {
    companion object {
        private const val TAG = "PlayMatchApp"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Intentar inicializar Firebase de forma segura
            try {
                val apps = FirebaseApp.getApps(this)
                if (apps.isEmpty()) {
                    Log.w(TAG, "Firebase no está inicializado. Verifica google-services.json")
                    Log.w(TAG, "La app puede funcionar pero las funciones de Firebase no estarán disponibles")
                } else {
                    Log.d(TAG, "Firebase inicializado correctamente")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al verificar Firebase", e)
                Log.e(TAG, "Continuando sin Firebase...")
            }
        } catch (e: Throwable) {
            // Capturar cualquier error, incluso errores de inicialización de Firebase
            Log.e(TAG, "Error crítico en onCreate", e)
            // No lanzar la excepción para permitir que la app arranque
        }
    }
}

