package com.example.playmatch.ui.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.playmatch.R
import com.example.playmatch.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        try {
            auth = FirebaseAuth.getInstance()
        } catch (e: Exception) {
            android.util.Log.e("RegisterFragment", "Error inicializando Firebase Auth", e)
            Toast.makeText(requireContext(), "Error: Firebase no está configurado correctamente", Toast.LENGTH_LONG).show()
        }
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        
        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        
        viewModel.registrationResult.observe(viewLifecycleOwner) { result ->
            // Rehabilitar botón
            binding.btnRegister.isEnabled = true
            binding.btnRegister.text = "Registrarse"
            
            result.getOrElse { error ->
                android.util.Log.e("RegisterFragment", "Error en registro", error)
                val errorMessage = error.message ?: "Error desconocido al registrar"
                Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_LONG).show()
                
                // Si el error es de Firebase no configurado, mostrar mensaje más claro
                if (errorMessage.contains("Firestore no está disponible") || 
                    errorMessage.contains("Firebase")) {
                    Toast.makeText(
                        requireContext(), 
                        "Firebase no está configurado. Configura google-services.json para continuar.", 
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@observe
            }
            
            val userId = result.getOrNull()
            if (userId == null) {
                android.util.Log.e("RegisterFragment", "userId es null después del registro exitoso")
                Toast.makeText(requireContext(), "Error: No se pudo obtener el ID de usuario", Toast.LENGTH_LONG).show()
                return@observe
            }
            
            android.util.Log.d("RegisterFragment", "Registro exitoso, userId=$userId")
            Toast.makeText(requireContext(), "Cuenta creada. Ahora inicia sesión.", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
        
        viewModel.emailExists.observe(viewLifecycleOwner) { exists ->
            if (exists) {
                binding.etEmail.error = "Este correo ya está registrado"
            }
        }
        
        viewModel.phoneExists.observe(viewLifecycleOwner) { exists ->
            if (exists) {
                binding.etPhone.error = "Este número ya está registrado"
            }
        }
    }
    
    private fun registerUser() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val phoneNumber = binding.etPhone.text.toString().trim()
        
        android.util.Log.d("RegisterFragment", "Intentando registrar: name=$name, email=$email, phone=$phoneNumber")
        
        if (validateInput(name, email, password, phoneNumber)) {
            // Deshabilitar botón mientras se procesa
            binding.btnRegister.isEnabled = false
            binding.btnRegister.text = "Verificando..."
            
            viewModel.checkEmailAndPhone(email, phoneNumber) { emailExists, phoneExists ->
                android.util.Log.d("RegisterFragment", "Verificación: emailExists=$emailExists, phoneExists=$phoneExists")
                
                if (!emailExists && !phoneExists) {
                    android.util.Log.d("RegisterFragment", "Datos válidos, registrando usuario...")
                    viewModel.registerUser(name, email, password, phoneNumber)
                } else {
                    // Rehabilitar botón
                    binding.btnRegister.isEnabled = true
                    binding.btnRegister.text = "Registrarse"
                    
                    if (emailExists) {
                        binding.etEmail.error = "Este correo ya está registrado"
                        android.util.Log.w("RegisterFragment", "Email ya existe: $email")
                    }
                    if (phoneExists) {
                        binding.etPhone.error = "Este número ya está registrado"
                        android.util.Log.w("RegisterFragment", "Teléfono ya existe: $phoneNumber")
                    }
                }
            }
        } else {
            android.util.Log.w("RegisterFragment", "Validación falló")
        }
    }
    
    private fun validateInput(name: String, email: String, password: String, phoneNumber: String): Boolean {
        var isValid = true
        
        if (TextUtils.isEmpty(name)) {
            binding.etName.error = "El nombre es requerido"
            isValid = false
        }
        
        if (TextUtils.isEmpty(email)) {
            binding.etEmail.error = "El correo es requerido"
            isValid = false
        }
        
        if (TextUtils.isEmpty(password)) {
            binding.etPassword.error = "La contraseña es requerida"
            isValid = false
        } else if (password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            isValid = false
        }
        
        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Correo inválido"
            isValid = false
        }
        
        if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.length < 10) {
            binding.etPhone.error = "Número inválido"
            isValid = false
        }
        
        return isValid
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

