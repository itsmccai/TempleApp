package com.example.tiktok

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiktok.Model.MarketModel
import com.example.tiktok.R
import com.example.tiktok.databinding.ItemLatestMarketBinding

class LatestMarketAdapter(private var itemList: List<MarketModel>) :
    RecyclerView.Adapter<LatestMarketAdapter.LatestMarketViewHolder>() {

    inner class LatestMarketViewHolder(val binding: ItemLatestMarketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestMarketViewHolder {
        val binding = ItemLatestMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LatestMarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LatestMarketViewHolder, position: Int) {
        val item = itemList[position]
        with(holder.binding) {
            // Existing code
            itemTitle.text = item.title
            itemPrice.text = "¥${item.price ?: 0.0}"
            Glide.with(itemImage.context).load(item.imageUrl).into(itemImage)

            // NEW: setup heart icon toggle
            likeIcon.setImageResource(R.drawable.unliked_market)  // default unliked
            likeIcon.tag = false  // false means unliked state

            likeIcon.setOnClickListener {
                val isLiked = it.tag as Boolean
                if (isLiked) {
                    likeIcon.setImageResource(R.drawable.unliked_market)  // unliked
                } else {
                    likeIcon.setImageResource(R.drawable.like_market)  // liked
                }
                it.tag = !isLiked
            }
        }
    }


    override fun getItemCount(): Int = itemList.size



}

