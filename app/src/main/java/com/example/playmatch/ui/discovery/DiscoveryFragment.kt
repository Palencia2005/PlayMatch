package com.example.playmatch.ui.discovery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmatch.R
import com.example.playmatch.databinding.FragmentDiscoveryBinding
import com.example.playmatch.data.model.UserProfile
import com.example.playmatch.data.repository.AuthRepository

class DiscoveryFragment : Fragment() {
    private var _binding: FragmentDiscoveryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DiscoveryViewModel by viewModels()
    private val authRepository = AuthRepository()
    private lateinit var profilesAdapter: ProfilesAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscoveryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        profilesAdapter = ProfilesAdapter { profile ->
            viewModel.likeProfile(profile)
        }
        
        binding.recyclerViewProfiles.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewProfiles.adapter = profilesAdapter
        
        binding.fabChats.setOnClickListener {
            findNavController().navigate(R.id.action_discoveryFragment_to_chatsFragment)
        }
        
        binding.fabProfile.setOnClickListener {
            val currentUserId = authRepository.getCurrentUserId()
            if (currentUserId != null) {
                try {
                    findNavController().navigate(
                        DiscoveryFragmentDirections.actionDiscoveryFragmentToProfileFragment(currentUserId)
                    )
                } catch (e: Exception) {
                    android.util.Log.e("DiscoveryFragment", "Error navegando a perfil", e)
                }
            } else {
                Toast.makeText(requireContext(), "Error: No se pudo obtener el ID de usuario", Toast.LENGTH_SHORT).show()
            }
        }
        
        viewModel.profiles.observe(viewLifecycleOwner) { profiles ->
            profilesAdapter.submitList(profiles)
        }
        
        viewModel.matchCreated.observe(viewLifecycleOwner) { matchId ->
            if (matchId.isNotEmpty()) {
                Toast.makeText(requireContext(), "¡Match! 🎉", Toast.LENGTH_LONG).show()
            }
        }
        
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId != null) {
            viewModel.loadProfiles(currentUserId)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



