package com.jocoweco.foodsommelier.data.feat.restaurant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jocoweco.foodsommelier.R

class MessageAdapter(private val items: List<ChatMessage>) :
    ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val View_User = 1
        private const val View_Ai = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].sender == Sender.USER) {
            View_User
        } else View_Ai
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return (if (viewType == View_User) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_ai, parent, false)
            AiViewHolder(view)
        })
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = getItem(position)
        if (holder is UserViewHolder) {
            holder.bind(chatMessage)
        } else if (holder is AiViewHolder) {
            holder.bind(chatMessage)
        }
    }

    override fun getItemCount(): Int = items.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message_user)
        fun bind(chatMessage: ChatMessage) {
            tvMessage.text = chatMessage.message
        }
    }

    class AiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tv_message_ai)
        fun bind(chatMessage: ChatMessage) {
            tvMessage.text = chatMessage.message
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }

}