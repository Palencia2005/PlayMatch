package com.example.playmatch.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.playmatch.R
import com.example.playmatch.data.repository.AuthRepository
import com.example.playmatch.databinding.FragmentProfileBinding
import com.google.android.material.chip.Chip

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val authRepository = AuthRepository()
    private val args: ProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun requireUserId(): String? {
        return args.userId ?: authRepository.getCurrentUserId()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = requireUserId()
        if (userId == null) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        viewModel.profileState.observe(viewLifecycleOwner) { state ->
            val user = state.user
            val profile = state.profile

            binding.tvName.text = buildString {
                append(user?.name ?: "Usuario")
                profile?.age?.takeIf { it > 0 }?.let {
                    append(", ")
                    append(it)
                }
            }
            binding.tvLookingFor.text =
                profile?.lookingFor?.takeIf { it.isNotEmpty() } ?: "¿Qué buscas?"
            binding.tvAgeDetail.text =
                getString(R.string.profile_age_format, profile?.age ?: 0)
            binding.tvGenderDetail.text =
                getString(R.string.profile_gender_format, profile?.gender ?: "-")
            binding.tvHeightDetail.text =
                getString(R.string.profile_height_format, profile?.height ?: "-")
            binding.tvPreferenceDetail.text =
                getString(R.string.profile_preference_format, profile?.preferredGender ?: "-")

            binding.chipGroupMusic.removeAllViews()
            val musicList = profile?.musicTaste.orEmpty()
            binding.tvMusicLabel.isVisible = musicList.isNotEmpty()
            binding.chipGroupMusic.isVisible = musicList.isNotEmpty()
            musicList.forEach { taste ->
                val chip = Chip(requireContext())
                chip.text = taste
                chip.isClickable = false
                chip.isCheckable = false
                chip.setChipBackgroundColorResource(R.color.white)
                binding.chipGroupMusic.addView(chip)
            }

            val profilePhoto = profile?.profilePhotoUrl
            val coverPhoto = profile?.coverPhotoUrl

            Glide.with(this)
                .load(coverPhoto.takeUnless { it.isNullOrEmpty() } ?: R.drawable.bg_gradient_fuchsia)
                .into(binding.imgCover)

            Glide.with(this)
                .load(profilePhoto.takeUnless { it.isNullOrEmpty() } ?: R.drawable.bg_profile_placeholder)
                .placeholder(R.drawable.bg_profile_placeholder)
                .into(binding.imgProfile)
        }

        binding.btnEditInfo.setOnClickListener {
            navigateToEditProfile()
        }

        binding.btnEditPhotos.setOnClickListener {
            navigateToEditPhotos()
        }

        viewModel.loadProfile(userId)
    }

    override fun onResume() {
        super.onResume()
        requireUserId()?.let { viewModel.loadProfile(it) }
    }

    private fun navigateToEditProfile() {
        val userId = authRepository.getCurrentUserId() ?: return
        try {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(userId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No se pudo abrir la edición", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun navigateToEditPhotos() {
        val userId = authRepository.getCurrentUserId() ?: return
        try {
            val action =
                ProfileFragmentDirections.actionProfileFragmentToPhotoUploadFragment(userId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "No se pudo abrir fotos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

