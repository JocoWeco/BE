package com.jocoweco.foodsommelier.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.jocoweco.foodsommelier.R
import com.jocoweco.foodsommelier.data.SharedPreferencesHelper
import com.jocoweco.foodsommelier.data.ai.GeminiChat
import com.jocoweco.foodsommelier.data.feat.restaurant.BestMenu
import com.jocoweco.foodsommelier.data.feat.restaurant.ChatMessage
import com.jocoweco.foodsommelier.data.feat.restaurant.Keyword
import com.jocoweco.foodsommelier.data.feat.restaurant.Location
import com.jocoweco.foodsommelier.data.feat.restaurant.MessageAdapter
import com.jocoweco.foodsommelier.data.feat.restaurant.Restaurant
import com.jocoweco.foodsommelier.data.feat.restaurant.Sender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject

class OrderTextFragment : Fragment(R.layout.fragment_order_text) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var adapter: MessageAdapter
    // private val messages = mutableListOf<ChatMessage>()

    // ViewModel 연동
    //private val chatViewModel: GeminiChat by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rv_chat)
        etMessage = view.findViewById(R.id.et_message_input)
        btnSend = view.findViewById(R.id.btn_send)

        adapter = MessageAdapter(emptyList())
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = false // 메시지 위에서부터 쌓이도록
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        btnSend.setOnClickListener {
            val text = etMessage.text.toString().trim()
            if (text.isNotEmpty()) {
                addMessage(Sender.USER, text)
                etMessage.text.clear()
                // 서버로 전송
                sendMessageToAI(text)
            }
        }
    }

    private fun addMessage(sender: Sender, message: String) {
        val currentList = adapter.currentList.toMutableList()
        currentList.add(ChatMessage(sender, message))
        adapter.submitList(currentList)
        recyclerView.scrollToPosition(currentList.size - 1)
    }

    // ai로 전송
    private fun sendMessageToAI(requestMessage: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val aiResponse = GeminiChat().sendMessage(requestMessage)

                // Firestore 저장
                val restaurants = parseAIResponseWithLocation(aiResponse)
                saveRestaurantsToFirestore(restaurants)

                withContext(Dispatchers.Main) {
                    addMessage(Sender.AI, "추천 결과가 저장되었어요!\nResult 화면에서 확인하세요.")
                    navigateToNextFragment()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "AI 호출 실패", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
            }
        }
    }

    // 프래그먼트 이동
    private fun navigateToNextFragment() {
        val nextFragment = ResultFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_result, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    // 파싱
    fun parseAIResponseWithLocation(jsonString: String): List<Restaurant> {
        val json = JSONObject(jsonString)
        val restaurants = mutableListOf<Restaurant>()

        for (i in 1..2) {
            val key = "restaurant$i"
            if (json.has(key)) {
                val r = json.getJSONObject(key)
                val name = r.getString("name")
                val address = r.getString("address")
                val locationJson = r.getJSONObject("location")
                val location = Location(
                    lat = locationJson.getDouble("lat"),
                    lon = locationJson.getDouble("lon")
                )
                val keyword = r.getJSONObject("keyword").let {
                    Keyword(
                        keyword1 = it.optString("keyword1"),
                        keyword2 = it.optString("keyword2"),
                        keyword3 = it.optString("keyword3"),
                        keyword4 = it.optString("keyword4")
                    )
                }
                val bestMenu = r.getJSONObject("bestMenu").let {
                    BestMenu(
                        menu1 = it.optString("menu1"),
                        menu2 = it.optString("menu2")
                    )
                }
                restaurants.add(
                    Restaurant(
                        name = name,
                        address = address,
                        location = location,
                        keyword = keyword,
                        bestMenu = bestMenu
                    )
                )
            }
        }
        return restaurants
    }

    // Firestore 저장
    suspend fun saveRestaurantsToFirestore(restaurants: List<Restaurant>) {
        val uuid = SharedPreferencesHelper.getUUID(requireContext())
        val db = FirebaseFirestore.getInstance()

        val collectionRef = db.collection("restaurants/$uuid/recent_restaurants")

        // 현재 최대 id 조회 후 +1
        val snapshot = collectionRef.orderBy(FieldPath.documentId()).limitToLast(1).get().await()
        var nextId = snapshot.documents.firstOrNull()?.getLong("id")?.plus(1) ?: 1L

        for (restaurant in restaurants) {
            collectionRef.document(nextId.toString()).set(restaurant).await()
            nextId++
        }
    }

}