//package com.example.tiktok
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.StaggeredGridLayoutManager
//import com.example.tiktok.Model.PostModel
//import com.example.tiktok.databinding.FragmentPostBinding
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.Query
//
////same as post fragment
//class MarketFragment : Fragment() {
//    private lateinit var binding: FragmentPostBinding
//    private val db = FirebaseFirestore.getInstance()
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentPostBinding.inflate(inflater, container, false)
//
//        binding.postRecyclerView.layoutManager =
//            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
//
//        // loading pics from collection products
//        db.collection("products")
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { result ->
//                val productPosts = result.mapNotNull { doc ->
//                    doc.toObject(PostModel::class.java)
//                }
//                binding.postRecyclerView.adapter = PostAdapter(productPosts)
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "failed to load products: ${it.message}", Toast.LENGTH_SHORT).show()
//            }
//
//
//        return binding.root
//    }
//}
package com.example.tiktok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktok.Model.MarketModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MarketFragment : Fragment() {

    private lateinit var searchBar: EditText
    private lateinit var marketRecyclerView: RecyclerView
    private lateinit var marketAdapter: MarketAdapter

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_market, container, false)

        // 初始化搜索栏和商品列表
        searchBar = view.findViewById(R.id.search_bar)
        marketRecyclerView = view.findViewById(R.id.marketRecyclerView)

        // 设置竖向商品列表
        marketRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 从 Firebase 加载标记为 product 的商品
        db.collection("products")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val productList = snapshot.mapNotNull { doc ->
                    doc.toObject(MarketModel::class.java)
                }.filter { it.isProduct }

                marketAdapter = MarketAdapter(productList)
                marketRecyclerView.adapter = marketAdapter
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "加载商品失败：${it.message}", Toast.LENGTH_SHORT).show()
            }

        return view
    }
}



