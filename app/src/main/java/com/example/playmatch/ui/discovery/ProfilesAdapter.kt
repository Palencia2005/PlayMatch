package com.example.playmatch.ui.discovery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.playmatch.R
import com.example.playmatch.data.model.UserProfile

class ProfilesAdapter(
    private val onLikeClick: (UserProfile) -> Unit
) : ListAdapter<UserProfile, ProfilesAdapter.ProfileViewHolder>(ProfileDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile, parent, false)
        return ProfileViewHolder(view, onLikeClick)
    }
    
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ProfileViewHolder(
        itemView: View,
        private val onLikeClick: (UserProfile) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvAge: TextView = itemView.findViewById(R.id.tvAge)
        private val tvBio: TextView = itemView.findViewById(R.id.tvBio)
        private val tvInterests: TextView = itemView.findViewById(R.id.tvInterests)
        private val btnLike: View = itemView.findViewById(R.id.btnLike)
        
        fun bind(profile: UserProfile) {
            tvAge.text = "${profile.age} años"
            tvBio.text = profile.bio
            tvInterests.text = profile.interests.joinToString(", ")
            
            btnLike.setOnClickListener {
                onLikeClick(profile)
            }
        }
    }
    
    class ProfileDiffCallback : DiffUtil.ItemCallback<UserProfile>() {
        override fun areItemsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem.userId == newItem.userId
        }
        
        override fun areContentsTheSame(oldItem: UserProfile, newItem: UserProfile): Boolean {
            return oldItem == newItem
        }
    }
}






