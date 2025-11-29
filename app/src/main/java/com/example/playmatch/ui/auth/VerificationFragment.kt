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
import com.example.playmatch.databinding.FragmentVerificationBinding

class VerificationFragment : Fragment() {
    private var _binding: FragmentVerificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: VerificationViewModel by viewModels()
    
    private val userId: String by lazy {
        arguments?.getString("userId") ?: ""
    }
    private val email: String by lazy {
        arguments?.getString("email") ?: ""
    }
    private val phoneNumber: String by lazy {
        arguments?.getString("phoneNumber") ?: ""
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Send verification code
        if (email.isNotEmpty()) {
            viewModel.sendEmailVerificationCode(email)
            binding.tvVerificationInfo.text = "Se ha enviado un código de verificación a $email"
        } else if (phoneNumber.isNotEmpty()) {
            viewModel.sendPhoneVerificationCode(phoneNumber, requireActivity())
            binding.tvVerificationInfo.text = "Se ha enviado un código de verificación a $phoneNumber"
        }
        
        binding.btnVerify.setOnClickListener {
            verifyCode()
        }
        
        binding.btnResend.setOnClickListener {
            if (email.isNotEmpty()) {
                viewModel.sendEmailVerificationCode(email)
            } else if (phoneNumber.isNotEmpty()) {
                viewModel.sendPhoneVerificationCode(phoneNumber, requireActivity())
            }
            Toast.makeText(requireContext(), "Código reenviado", Toast.LENGTH_SHORT).show()
        }
        
        viewModel.verificationResult.observe(viewLifecycleOwner) { result ->
            result.getOrElse { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_LONG).show()
                return@observe
            }
            Toast.makeText(requireContext(), "Verificación exitosa", Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                VerificationFragmentDirections.actionVerificationFragmentToProfileSetupFragment(userId)
            )
        }
    }
    
    private fun verifyCode() {
        val code = binding.etCode.text.toString().trim()
        
        if (TextUtils.isEmpty(code)) {
            binding.etCode.error = "El código es requerido"
            return
        }
        
        if (email.isNotEmpty()) {
            viewModel.verifyEmailCode(code)
        } else if (phoneNumber.isNotEmpty()) {
            viewModel.verifyPhoneCode(code)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

