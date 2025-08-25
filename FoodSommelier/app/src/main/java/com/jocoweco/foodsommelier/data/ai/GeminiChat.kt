package com.jocoweco.foodsommelier.data.ai//package com.jocoweco.foodsommelier.data.ai

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.jocoweco.foodsommelier.BuildConfig
import com.jocoweco.foodsommelier.data.feat.restaurant.ChatMessage
import com.jocoweco.foodsommelier.data.feat.restaurant.Sender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GeminiChat : ViewModel() {
    private val _chatMessages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val chatMessages: LiveData<MutableList<ChatMessage>> get() = _chatMessages

    val apiKey = BuildConfig.GEMINI_API_KEY

    // 생성 모델 초기화
    val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(
            modelName = "gemini-2.0-flash-exp",
            generationConfig = generationConfig {
                responseModalities = listOf(ResponseModality.TEXT)
                temperature = 0.7f
                maxOutputTokens = 2000
            })

    // 메세지 전송
    suspend fun sendMessage(input: String): String {
        val userMessage = ChatMessage(Sender.USER, input)
        _chatMessages.value?.add(userMessage)
        withContext(Dispatchers.Main) {
            _chatMessages.notifyObserver()
        }

        // 프롬프트 생성
        val promptText = buildPrompt(_chatMessages.value ?: listOf())
        val promptContent = content("user") { text(promptText) }

        // 비동기 호출
        return try {
            val response = model.generateContent(promptContent)
            val aiText = response.text ?: "응답 없음"

            val aiMessage = ChatMessage(Sender.AI, aiText)
            _chatMessages.value?.add(aiMessage)
            _chatMessages.notifyObserver()
            aiText
        } catch (e: Exception) {
            val errorMessage = ChatMessage(Sender.AI, "AI 요청 실패: ${e.message}")
            _chatMessages.value?.add(errorMessage)
            _chatMessages.notifyObserver()
            "AI 요청 실패: ${e.message}"
        }

    }

    // 프롬프트 생성
    private fun buildPrompt(messages: List<ChatMessage>): String {
        val recentMessages = messages.takeLast(4) // 2번 왕복
        val userText = recentMessages.filter { it.sender == Sender.USER }
            .joinToString("\n") { "User:${it.message}" }

        val instruction = """
         You are a local restaurant recommendation assistant.
            Extract 3–4 key keywords from the user's messages.
            Recommend 2 nearby restaurants (within 30 min walk) matching preferences:
            food type, excluded ingredients, spice level, age, gender.
            Output must be valid JSON in the input language,
            including restaurant name, address, coordinates,
            3–4 descriptive keywords, and 2 best menu items (excluding banned ingredients).
    """.trimIndent()

        return "$instruction\n$userText"
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }
}
