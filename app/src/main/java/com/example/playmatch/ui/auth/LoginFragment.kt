package com.example.playmatch.ui.auth

import android.os.Bundle
import android.view.animation.AccelerateDecelerateInterpolator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.playmatch.R
import com.example.playmatch.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentLoginBinding.inflate(inflater, container, false)
            return binding.root
        } catch (e: Exception) {
            android.util.Log.e("LoginFragment", "Error al crear vista", e)
            // Crear una vista simple si hay un error
            val view = android.widget.TextView(requireContext())
            view.text = "Error al cargar la interfaz. Verifica los recursos."
            return view
        }
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        try {
            binding.contentContainer.translationY = 60f
            binding.contentContainer.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(600)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
            
            binding.btnLogin.setOnClickListener {
                loginUser()
            }
            
            binding.tvRegister.setOnClickListener {
                try {
                    findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
                } catch (e: Exception) {
                    android.util.Log.e("LoginFragment", "Error en navegación", e)
                    Toast.makeText(requireContext(), "Error de navegación", Toast.LENGTH_SHORT).show()
                }
            }
            
            binding.tvForgotPassword.setOnClickListener {
                showForgotPasswordDialog()
            }
            
            viewModel.loginResult.observe(viewLifecycleOwner) { result ->
                result.getOrElse { error ->
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    return@observe
                }
                
                val userId = result.getOrNull() ?: return@observe
                Toast.makeText(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                
                val hasProfile = viewModel.hasProfile.value == true
                try {
                    if (hasProfile) {
                        findNavController().navigate(R.id.action_loginFragment_to_discoveryFragment)
                    } else {
                        findNavController().navigate(
                            LoginFragmentDirections.actionLoginFragmentToProfileSetupFragment(userId)
                        )
                    }
                } catch (e: Exception) {
                    android.util.Log.e("LoginFragment", "Error navegando", e)
                    try {
                        findNavController().navigate(R.id.action_loginFragment_to_discoveryFragment)
                    } catch (e2: Exception) {
                        android.util.Log.e("LoginFragment", "Error navegando a discovery", e2)
                    }
                }
            }
            
            viewModel.passwordResetResult.observe(viewLifecycleOwner) { result ->
                result.getOrElse { error ->
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                    return@observe
                }
                Toast.makeText(requireContext(), "Se ha enviado un correo para recuperar tu contraseña", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            android.util.Log.e("LoginFragment", "Error en onViewCreated", e)
            Toast.makeText(requireContext(), "Error al inicializar: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (validateInput(email, password)) {
            viewModel.login(email, password)
        }
    }
    
    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true
        
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.error = "El correo es requerido"
            isValid = false
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.error = "La contraseña es requerida"
            isValid = false
        }
        
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Correo inválido"
            isValid = false
        }
        
        return isValid
    }
    
    private fun showForgotPasswordDialog() {
        val email = binding.etEmail.text.toString().trim()
        
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Recuperar Contraseña")
        
        val input = android.widget.EditText(requireContext())
        input.hint = "Correo electrónico"
        input.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        if (email.isNotEmpty()) {
            input.setText(email)
        }
        
        builder.setView(input)
        builder.setPositiveButton("Enviar") { _, _ ->
            val emailToSend = input.text.toString().trim()
            if (emailToSend.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(emailToSend).matches()) {
                viewModel.sendPasswordResetEmail(emailToSend)
            } else {
                Toast.makeText(requireContext(), "Ingresa un correo válido", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

