package com.kappa.app.audio.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kappa.app.R

data class UserPost(
    val id: String,
    val userName: String,
    val content: String,
    val imageRes: Int,
    val avatarRes: Int = R.drawable.ic_profile
)

class PostsAdapter(
    private val onClick: (UserPost) -> Unit
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    private val items = mutableListOf<UserPost>()

    fun submitList(posts: List<UserPost>) {
        items.clear()
        items.addAll(posts)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class PostViewHolder(
        itemView: View,
        private val onClick: (UserPost) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.image_post_media)
        private val avatar = itemView.findViewById<ImageView>(R.id.image_post_avatar)
        private val user = itemView.findViewById<TextView>(R.id.text_post_user)
        private val content = itemView.findViewById<TextView>(R.id.text_post_content)

        fun bind(post: UserPost) {
            image.setImageResource(post.imageRes)
            avatar.setImageResource(post.avatarRes)
            user.text = post.userName
            content.text = post.content
            itemView.setOnClickListener { onClick(post) }
        }
    }
}
