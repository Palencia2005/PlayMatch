package com.example.playmatch.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playmatch.R
import com.example.playmatch.data.repository.ProfileRepository
import com.example.playmatch.databinding.FragmentPhotoUploadBinding
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

private enum class PhotoType(val fileName: String) {
    COVER("cover.jpg"),
    PROFILE("profile.jpg")
}

class PhotoUploadFragment : Fragment() {
    private var _binding: FragmentPhotoUploadBinding? = null
    private val binding get() = _binding!!
    private val profileRepository = ProfileRepository()
    private val storage by lazy { FirebaseStorage.getInstance().reference }

    private val userId: String by lazy {
        arguments?.getString("userId") ?: ""
    }

    private val pickCoverLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uploadPhoto(it, PhotoType.COVER) }
        }

    private val pickProfileLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { uploadPhoto(it, PhotoType.PROFILE) }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userId.isEmpty()) {
            Toast.makeText(requireContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        binding.btnPickCover.setOnClickListener {
            pickCoverLauncher.launch("image/*")
        }

        binding.btnPickProfile.setOnClickListener {
            pickProfileLauncher.launch("image/*")
        }

        binding.btnSkip.setOnClickListener {
            navigateToDiscovery()
        }

        binding.btnContinue.setOnClickListener {
            navigateToDiscovery()
        }

        loadCurrentPhotos()
    }

    private fun loadCurrentPhotos() {
        viewLifecycleOwner.lifecycleScope.launch {
            val profile = profileRepository.getProfile(userId)
            profile?.coverPhotoUrl?.takeIf { it.isNotEmpty() }?.let { url ->
                Glide.with(requireContext())
                    .load(url)
                    .into(binding.imgCoverPreview)
            }
            profile?.profilePhotoUrl?.takeIf { it.isNotEmpty() }?.let { url ->
                Glide.with(requireContext())
                    .load(url)
                    .placeholder(R.drawable.bg_profile_placeholder)
                    .into(binding.imgProfilePreview)
            }
        }
    }

    private fun uploadPhoto(uri: Uri, type: PhotoType) {
        binding.progressBar.isVisible = true
        val photoRef = storage.child("profiles/$userId/${type.fileName}")
        photoRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Error subiendo imagen")
                }
                photoRef.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                viewLifecycleOwner.lifecycleScope.launch {
                    val result = profileRepository.updateProfilePhotos(
                        userId = userId,
                        profilePhotoUrl = if (type == PhotoType.PROFILE) downloadUri.toString() else null,
                        coverPhotoUrl = if (type == PhotoType.COVER) downloadUri.toString() else null
                    )
                    binding.progressBar.isVisible = false
                    result.onSuccess { updated ->
                        Toast.makeText(requireContext(), "Foto actualizada", Toast.LENGTH_SHORT)
                            .show()
                        when (type) {
                            PhotoType.COVER -> {
                                Glide.with(requireContext())
                                    .load(updated.coverPhotoUrl)
                                    .into(binding.imgCoverPreview)
                            }
                            PhotoType.PROFILE -> {
                                Glide.with(requireContext())
                                    .load(updated.profilePhotoUrl)
                                    .placeholder(R.drawable.bg_profile_placeholder)
                                    .into(binding.imgProfilePreview)
                            }
                        }
                    }.onFailure { error ->
                        Toast.makeText(
                            requireContext(),
                            "No se pudo guardar la foto: ${error.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToDiscovery() {
        findNavController().navigate(R.id.action_photoUploadFragment_to_discoveryFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

