package com.example.tiktok
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiktok.Model.MarketModel
import com.example.tiktok.R
import com.example.tiktok.LatestMarketAdapter.LatestMarketViewHolder
import com.example.tiktok.Util.PostDetailActivity
import com.example.tiktok.databinding.ItemMarketBinding

class MarketAdapter(private var itemList: List<MarketModel>) :
    RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {

    inner class MarketViewHolder(val binding: ItemMarketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val binding = ItemMarketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        val item = itemList[position]
        with(holder.binding) {
            itemTitle.text = item.title
            itemPrice.text = "¥${item.price ?: 0.0}"
            itemDescription.text = item.caption
            Glide.with(itemImage.context).load(item.imageUrl).into(itemImage)

            // like button
            likeIcon.setImageResource(R.drawable.unliked_market)
            likeIcon.tag = false
            likeIcon.setOnClickListener {
                val isLiked = it.tag as Boolean
                likeIcon.setImageResource(
                    if (isLiked) R.drawable.unliked_market else R.drawable.like_market
                )
                it.tag = !isLiked
            }

            // jump to detail
            root.setOnClickListener {
                val context = holder.itemView.context
                val intent = Intent(context, PostDetailActivity::class.java)
                intent.putExtra("post", item)
                context.startActivity(intent)
            }
        }
    }
    fun updateList(newList: List<MarketModel>) {
        itemList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = itemList.size
}
