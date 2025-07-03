package com.example.tiktok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.tiktok.Model.PostModel
import com.example.tiktok.databinding.FragmentPostBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)

        binding.postRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // 从 Firestore 加载 posts 集合
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.mapNotNull { doc ->
                    doc.toObject(PostModel::class.java)
                }
                binding.postRecyclerView.adapter = PostAdapter(posts)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "加载失败: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        return binding.root
    }
}
