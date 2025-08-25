package com.jocoweco.foodsommelier.fragment

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.jocoweco.foodsommelier.R
import com.jocoweco.foodsommelier.data.SharedPreferencesHelper
import com.jocoweco.foodsommelier.data.feat.restaurant.Restaurant
import com.jocoweco.foodsommelier.data.feat.restaurant.RestaurantAdapter

class ResultFragment : Fragment(R.layout.fragment_result) {

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: RestaurantAdapter
    private lateinit var restaurants: List<Restaurant>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rv_found_restaurants)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = RestaurantAdapter(restaurants) { restaurants ->
            // 지도에 위치 표시

            // 화면 보이기

        }
        recyclerView.adapter = adapter

        loadRestaurantsFromFirestore()
    }

    private fun loadRestaurantsFromFirestore() {
        val uuid = SharedPreferencesHelper.getUUID(requireContext())
        val db = FirebaseFirestore.getInstance()

        val collectionRef = db.collection("restaurants/$uuid/recent_restaurants")
            .get()
            .addOnSuccessListener { snapshot ->
                restaurants = snapshot.documents.mapNotNull { it.toObject(Restaurant::class.java) }
                adapter.updateData(restaurants)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "데이터 불러오기 실패: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}