package com.example.playmatch.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmatch.R
import com.example.playmatch.databinding.FragmentChatsBinding
import com.example.playmatch.data.repository.AuthRepository

class ChatsFragment : Fragment() {
    private var _binding: FragmentChatsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatsViewModel by viewModels()
    private val authRepository = AuthRepository()
    private lateinit var chatsAdapter: ChatsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        chatsAdapter = ChatsAdapter { chatId ->
            findNavController().navigate(
                ChatsFragmentDirections.actionChatsFragmentToChatDetailFragment(chatId)
            )
        }
        
        binding.recyclerViewChats.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewChats.adapter = chatsAdapter
        
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId != null) {
            viewModel.loadChats(currentUserId)
        }
        
        viewModel.chats.observe(viewLifecycleOwner) { chats ->
            chatsAdapter.submitList(chats)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






