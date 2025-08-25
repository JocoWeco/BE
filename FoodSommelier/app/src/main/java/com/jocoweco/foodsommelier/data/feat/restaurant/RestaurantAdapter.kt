package com.jocoweco.foodsommelier.data.feat.restaurant


import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
//import com.bumptech.glide.Glide
import com.jocoweco.foodsommelier.R

class RestaurantAdapter(
    private var items: List<Restaurant>,
    private val onClick: (Restaurant) -> Unit
) :
    RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(items[position], onClick)
    }

    override fun getItemCount(): Int = items.size

    class RestaurantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // XML의 뷰 ID와 일치해야 합니다.
        private val restaurantName: TextView = itemView.findViewById(R.id.tv_retaurant_name)
        private val restaurantAddress: TextView = itemView.findViewById(R.id.tv_address)
        private val restaurantKeyword: TextView = itemView.findViewById(R.id.tv_keywords)
        private val restaurantMenu: TextView = itemView.findViewById(R.id.tv_menu)

        fun bind(restaurant: Restaurant, onClick: (Restaurant) -> Unit) {
            restaurantName.text = restaurant.name
            restaurantAddress.text = restaurant.address
            restaurantKeyword.text =
                "${restaurant.keyword.keyword1}, ${restaurant.keyword.keyword2}, " +
                        "${restaurant.keyword.keyword3}, ${restaurant.keyword.keyword4}"

            restaurantMenu.text = "추천: ${restaurant.bestMenu.menu1}, ${restaurant.bestMenu.menu2}"

            itemView.setOnClickListener {
                onClick(restaurant)

            }

        }
    }

    fun updateData(newItems: List<Restaurant>) {
        items = newItems
        notifyDataSetChanged()
    }
}