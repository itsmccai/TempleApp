package com.example.tiktok

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.tiktok.Model.PostModel
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiktok.Util.PostDetailActivity

//ViewHolder 是包含列表中各列表项的布局的 View 的封装容器
//Adapter 会根据需要创建 ViewHolder 对象，还会为这些视图设置数据。将视图与其数据相关联的过程称为“绑定
//简单来讲， viewholder控制每个卡片的布局，adapter将数据传给每个卡片是实现绑定

class PostAdapter(private val posts: List<PostModel>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>()
{

     class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         //先找到要卡片中有的布局信息
        val postImage: ImageView = view.findViewById(R.id.postImage)
        val postTitle: TextView = view.findViewById(R.id.postTitle)
         val priceTag:TextView = view.findViewById(R.id.priceTag)

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //获取dataset中的信息然后替换
        val post = posts[position]
        //use Glide to load pics
        Glide.with(holder.itemView.context)
            .load(post.imageUrl)
            .into(holder.postImage)
        holder.postTitle.text = post.title

        // 标记为商品时才显示价格
        if (post.isProduct && post.price != null) {
            holder.priceTag.text = "¥${post.price}"
            holder.priceTag.visibility = View.VISIBLE
        } else {
            holder.priceTag.visibility = View.GONE
        }





        //点击主页的post后跳转其detail页面
        holder.itemView.setOnClickListener{
//            val context = holder.itemView.context
//            val intent = Intent(context, PostDetailActivity::class.java).apply{
//                putExtra("postImageUrl",post.imageUrl)
//                putExtra("postTitle",post.title)
//                putExtra("postCaption",post.caption)
//                putExtra("userID",post.userId)
//            }
//            context.startActivity(intent)
            val context = holder.itemView.context
            val intent = Intent(context, PostDetailActivity::class.java)
            intent.putExtra("post", post)  // 确保 PostModel 实现 Parcelable
            context.startActivity(intent)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int = posts.size
}
