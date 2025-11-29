package com.example.playmatch.ui.profile

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
import com.example.playmatch.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EditProfileViewModel by viewModels()
    
    private val userId: String by lazy {
        arguments?.getString("userId") ?: ""
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Cargar perfil existente
        viewModel.loadProfile(userId)
        
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            profile?.let {
                binding.etAge.setText(it.age.toString())
                binding.etGender.setText(it.gender)
                binding.etHeight.setText(it.height)
                binding.etMusicTaste.setText(it.musicTaste.joinToString(", "))
                binding.etLookingFor.setText(it.lookingFor)
                binding.etPreferredGender.setText(it.preferredGender)
            }
        }
        
        binding.btnSave.setOnClickListener {
            saveProfile()
        }
        
        viewModel.profileSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(requireContext(), "Perfil actualizado exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }
    
    private fun saveProfile() {
        val ageText = binding.etAge.text.toString().trim()
        val age = ageText.toIntOrNull()
        
        if (TextUtils.isEmpty(ageText) || age == null) {
            binding.etAge.error = "La edad es requerida"
            return
        }
        
        if (age < 18 || age > 100) {
            binding.etAge.error = "La edad debe ser entre 18 y 100 años"
            return
        }
        
        val gender = binding.etGender.text.toString().trim()
        if (TextUtils.isEmpty(gender)) {
            binding.etGender.error = "El sexo es requerido"
            return
        }
        
        val height = binding.etHeight.text.toString().trim()
        val musicTaste = binding.etMusicTaste.text.toString().trim()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        val lookingFor = binding.etLookingFor.text.toString().trim()
        
        val preferredGender = binding.etPreferredGender.text.toString().trim()
        if (preferredGender.isEmpty()) {
            binding.etPreferredGender.error = "Especifica con quién deseas relacionarte"
            return
        }

        viewModel.updateProfile(
            userId = userId,
            age = age,
            gender = gender,
            height = height,
            musicTaste = musicTaste,
            lookingFor = lookingFor,
            preferredGender = preferredGender
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

