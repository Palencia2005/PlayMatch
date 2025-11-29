package com.example.playmatch.ui.chat

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playmatch.databinding.FragmentChatDetailBinding
import com.example.playmatch.data.repository.AuthRepository
import java.util.UUID

class ChatDetailFragment : Fragment() {
    private var _binding: FragmentChatDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatDetailViewModel by viewModels()
    private val authRepository = AuthRepository()
    private lateinit var messagesAdapter: MessagesAdapter
    
    private val chatId: String by lazy {
        arguments?.getString("chatId") ?: ""
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        messagesAdapter = MessagesAdapter(authRepository.getCurrentUserId() ?: "")
        
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMessages.adapter = messagesAdapter
        
        binding.btnSend.setOnClickListener {
            sendMessage()
        }
        
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messagesAdapter.submitList(messages)
            if (messages.isNotEmpty()) {
                binding.recyclerViewMessages.smoothScrollToPosition(messages.size - 1)
            }
        }
        
        viewModel.loadMessages(chatId)
    }
    
    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (TextUtils.isEmpty(text)) {
            return
        }
        
        val currentUserId = authRepository.getCurrentUserId() ?: return
        viewModel.sendMessage(chatId, currentUserId, text)
        binding.etMessage.setText("")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






