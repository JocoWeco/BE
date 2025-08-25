package com.jocoweco.foodsommelier.data.feat.restaurant

enum class Sender {
    AI, USER
}

data class ChatMessage(
    val sender: Sender,
    val message:String,
    val timestamp: Long = System.currentTimeMillis()
)