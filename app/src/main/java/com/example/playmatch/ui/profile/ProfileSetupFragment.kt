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
import com.example.playmatch.ui.profile.ProfileSetupFragmentDirections
import com.example.playmatch.R
import com.example.playmatch.databinding.FragmentProfileSetupBinding

class ProfileSetupFragment : Fragment() {
    private var _binding: FragmentProfileSetupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileSetupViewModel by viewModels()
    
    private val userId: String by lazy {
        arguments?.getString("userId") ?: ""
    }
    
    private var currentQuestion = 0
    private var selectedGender: String? = null
    private var selectedPreferredGender: String? = null
    private var selectedHeight: String? = null
    private var selectedMusicTaste = mutableListOf<String>()
    private var selectedLookingFor: String? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSetupBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        showQuestion(currentQuestion)
        
        binding.btnNext.setOnClickListener {
            handleAnswer()
        }
        
        viewModel.profileSaved.observe(viewLifecycleOwner) { saved ->
            if (saved) {
                Toast.makeText(requireContext(), "Perfil creado exitosamente", Toast.LENGTH_SHORT).show()
                try {
                    val action = ProfileSetupFragmentDirections
                        .actionProfileSetupFragmentToProfileFragment(userId)
                    findNavController().navigate(action)
                } catch (e: Exception) {
                    findNavController().navigate(R.id.action_profileSetupFragment_to_profileFragment)
                }
            }
        }
    }
    
    private fun showQuestion(index: Int) {
        binding.tvProgress.text = "${index + 1}/6"
        
        // Ocultar contenedor de opciones y mostrar input
        binding.optionsContainer.visibility = View.GONE
        binding.tilAnswer.visibility = View.VISIBLE
        
        when (index) {
            0 -> {
                binding.tvQuestion.text = "¿Cuál es tu edad?"
                binding.etAnswer.hint = "Ingresa tu edad"
                binding.etAnswer.inputType = android.text.InputType.TYPE_CLASS_NUMBER
                binding.etAnswer.setText("")
            }
            1 -> {
                binding.tvQuestion.text = "¿Cuál es tu sexo?"
                selectedGender = null
                showGenderOptions()
            }
            2 -> {
                binding.tvQuestion.text = "¿Con qué género deseas relacionarte?"
                selectedPreferredGender = null
                showPreferredGenderOptions()
            }
            3 -> {
                binding.tvQuestion.text = "¿Cuál es tu estatura?"
                selectedHeight = null
                showHeightOptions()
            }
            4 -> {
                binding.tvQuestion.text = "¿Cuáles son tus gustos musicales?"
                selectedMusicTaste.clear()
                showMusicOptions()
            }
            5 -> {
                binding.tvQuestion.text = "¿Qué buscas en la aplicación?"
                selectedLookingFor = null
                showLookingForOptions()
            }
        }
        
        if (index == 5) {
            binding.btnNext.text = "Finalizar"
        } else {
            binding.btnNext.text = "Siguiente"
        }
    }

    private fun showPreferredGenderOptions() {
        binding.optionsContainer.removeAllViews()
        binding.optionsContainer.visibility = View.VISIBLE
        binding.tilAnswer.visibility = View.GONE

        val options = listOf("Hombres", "Mujeres", "Todos")
        val radioGroup = android.widget.RadioGroup(requireContext()).apply {
            orientation = android.widget.RadioGroup.VERTICAL
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        options.forEach { option ->
            val radioButton = com.google.android.material.radiobutton.MaterialRadioButton(requireContext()).apply {
                text = option
                setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE))
                id = View.generateViewId()
                layoutParams = android.view.ViewGroup.MarginLayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                setPadding(16, 16, 16, 16)
            }

            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedPreferredGender = option
                }
            }

            radioGroup.addView(radioButton)
        }

        binding.optionsContainer.addView(radioGroup)
    }
    
    private fun showGenderOptions() {
        binding.optionsContainer.removeAllViews()
        binding.optionsContainer.visibility = View.VISIBLE
        binding.tilAnswer.visibility = View.GONE
        
        val options = listOf("Masculino", "Femenino", "Otro")
        val radioGroup = android.widget.RadioGroup(requireContext()).apply {
            orientation = android.widget.RadioGroup.VERTICAL
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        options.forEach { option ->
            val radioButton = com.google.android.material.radiobutton.MaterialRadioButton(requireContext()).apply {
                text = option
                setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE))
                id = View.generateViewId()
                layoutParams = android.view.ViewGroup.MarginLayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                setPadding(16, 16, 16, 16)
            }
            
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedGender = option
                }
            }
            
            radioGroup.addView(radioButton)
        }
        
        binding.optionsContainer.addView(radioGroup)
    }
    
    private fun showHeightOptions() {
        binding.optionsContainer.removeAllViews()
        binding.optionsContainer.visibility = View.VISIBLE
        binding.tilAnswer.visibility = View.GONE
        
        val options = listOf("150-160 cm", "161-170 cm", "171-180 cm", "181-190 cm", "191+ cm")
        val radioGroup = android.widget.RadioGroup(requireContext()).apply {
            orientation = android.widget.RadioGroup.VERTICAL
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        options.forEach { option ->
            val radioButton = com.google.android.material.radiobutton.MaterialRadioButton(requireContext()).apply {
                text = option
                setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE))
                id = View.generateViewId()
                layoutParams = android.view.ViewGroup.MarginLayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                setPadding(16, 16, 16, 16)
            }
            
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedHeight = option
                }
            }
            
            radioGroup.addView(radioButton)
        }
        
        binding.optionsContainer.addView(radioGroup)
    }
    
    private fun showMusicOptions() {
        binding.optionsContainer.removeAllViews()
        binding.optionsContainer.visibility = View.VISIBLE
        binding.tilAnswer.visibility = View.GONE
        
        selectedMusicTaste.clear()
        
        val options = listOf("Rock", "Pop", "Reggaeton", "Salsa", "Hip Hop", "Electrónica", "Jazz", "Clásica")
        
        options.forEach { option ->
            val checkBox = com.google.android.material.checkbox.MaterialCheckBox(requireContext()).apply {
                text = option
                setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE))
                id = View.generateViewId()
                layoutParams = android.view.ViewGroup.MarginLayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                setPadding(16, 16, 16, 16)
            }
            
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedMusicTaste.add(option)
                } else {
                    selectedMusicTaste.remove(option)
                }
            }
            
            binding.optionsContainer.addView(checkBox)
        }
    }
    
    private fun showLookingForOptions() {
        binding.optionsContainer.removeAllViews()
        binding.optionsContainer.visibility = View.VISIBLE
        binding.tilAnswer.visibility = View.GONE
        
        val options = listOf("Amistad", "Relación seria", "Casual", "Conocer gente", "Networking")
        val radioGroup = android.widget.RadioGroup(requireContext()).apply {
            orientation = android.widget.RadioGroup.VERTICAL
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        
        options.forEach { option ->
            val radioButton = com.google.android.material.radiobutton.MaterialRadioButton(requireContext()).apply {
                text = option
                setTextColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.WHITE))
                id = View.generateViewId()
                layoutParams = android.view.ViewGroup.MarginLayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 8, 0, 8)
                }
                setPadding(16, 16, 16, 16)
            }
            
            radioButton.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedLookingFor = option
                }
            }
            
            radioGroup.addView(radioButton)
        }
        
        binding.optionsContainer.addView(radioGroup)
    }
    
    private fun handleAnswer() {
        when (currentQuestion) {
            0 -> {
                val answer = binding.etAnswer.text.toString().trim()
                if (TextUtils.isEmpty(answer)) {
                    Toast.makeText(requireContext(), "Por favor ingresa tu edad", Toast.LENGTH_SHORT).show()
                    return
                }
                val age = answer.toIntOrNull()
                if (age == null) {
                    Toast.makeText(requireContext(), "Por favor ingresa una edad válida", Toast.LENGTH_SHORT).show()
                    return
                }
                if (age < 18) {
                    Toast.makeText(requireContext(), "Debes ser mayor de 18 años para usar esta aplicación", Toast.LENGTH_LONG).show()
                    // Cerrar la aplicación o volver al login
                    requireActivity().finish()
                    return
                }
                if (age > 100) {
                    Toast.makeText(requireContext(), "Por favor ingresa una edad válida", Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.setAge(age)
            }
            1 -> {
                if (selectedGender == null) {
                    Toast.makeText(requireContext(), "Por favor selecciona una opción", Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.setGender(selectedGender!!)
            }
            2 -> {
                if (selectedPreferredGender == null) {
                    Toast.makeText(requireContext(), "Por favor selecciona una opción", Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.setPreferredGender(selectedPreferredGender!!)
            }
            3 -> {
                if (selectedHeight == null) {
                    Toast.makeText(requireContext(), "Por favor selecciona una opción", Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.setHeight(selectedHeight!!)
            }
            4 -> {
                if (selectedMusicTaste.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor selecciona al menos un gusto musical", Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.setMusicTaste(selectedMusicTaste.toList())
            }
            5 -> {
                if (selectedLookingFor == null) {
                    Toast.makeText(requireContext(), "Por favor selecciona una opción", Toast.LENGTH_SHORT).show()
                    return
                }
                viewModel.setLookingFor(selectedLookingFor!!)
            }
        }
        
        if (currentQuestion < 5) {
            currentQuestion++
            showQuestion(currentQuestion)
        } else {
            // Guardar perfil automáticamente y luego ir a subir fotos
            viewModel.saveProfile(userId)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



