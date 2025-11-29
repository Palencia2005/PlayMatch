package com.example.playmatch.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.playmatch.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Animación de entrada: fade in + scale up
        val fadeIn = ObjectAnimator.ofFloat(binding.tvAppName, "alpha", 0f, 1f).apply {
            duration = 800
        }
        
        val scaleX = ObjectAnimator.ofFloat(binding.tvAppName, "scaleX", 0.5f, 1f).apply {
            duration = 1000
        }
        
        val scaleY = ObjectAnimator.ofFloat(binding.tvAppName, "scaleY", 0.5f, 1f).apply {
            duration = 1000
        }
        
        val enterAnimation = AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY)
        }
        
        enterAnimation.start()
        
        // Después de la animación de entrada, agregar pulso continuo
        Handler(Looper.getMainLooper()).postDelayed({
            val pulseX = ObjectAnimator.ofFloat(binding.tvAppName, "scaleX", 1f, 1.1f, 1f).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
            }
            
            val pulseY = ObjectAnimator.ofFloat(binding.tvAppName, "scaleY", 1f, 1.1f, 1f).apply {
                duration = 1000
                repeatCount = ObjectAnimator.INFINITE
            }
            
            val pulseAnimation = AnimatorSet().apply {
                playTogether(pulseX, pulseY)
            }
            
            pulseAnimation.start()
        }, 1000)
        
        // Esperar 2.5 segundos y luego navegar
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }
}

