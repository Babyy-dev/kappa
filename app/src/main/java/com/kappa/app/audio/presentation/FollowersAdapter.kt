package com.kappa.app.audio.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kappa.app.R

data class FollowerItem(
    val id: String,
    val name: String,
    val meta: String,
    val badge: String? = null,
    val avatarRes: Int = R.drawable.ic_profile
)

class FollowersAdapter(
    private val onClick: (FollowerItem) -> Unit
) : RecyclerView.Adapter<FollowersAdapter.FollowerViewHolder>() {

    private val items = mutableListOf<FollowerItem>()

    fun submitList(followers: List<FollowerItem>) {
        items.clear()
        items.addAll(followers)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_follower, parent, false)
        return FollowerViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: FollowerViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class FollowerViewHolder(
        itemView: View,
        private val onClick: (FollowerItem) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val avatar = itemView.findViewById<ImageView>(R.id.image_follower_avatar)
        private val name = itemView.findViewById<TextView>(R.id.text_follower_name)
        private val meta = itemView.findViewById<TextView>(R.id.text_follower_meta)
        private val badge = itemView.findViewById<TextView>(R.id.text_follower_badge)

        fun bind(item: FollowerItem) {
            avatar.setImageResource(item.avatarRes)
            name.text = item.name
            meta.text = item.meta
            if (item.badge.isNullOrBlank()) {
                badge.visibility = View.GONE
            } else {
                badge.visibility = View.VISIBLE
                badge.text = item.badge
            }
            itemView.setOnClickListener { onClick(item) }
        }
    }
}
