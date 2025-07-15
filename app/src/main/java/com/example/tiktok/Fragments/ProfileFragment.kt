package com.example.tiktok

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.tiktok.Model.PostModel
import com.example.tiktok.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // 登出按钮
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), LoginActicity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // 初始化瀑布布局
        binding.recyclerMyPosts.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // 加载用户信息和帖子
        loadUserInfo()
        loadUserPosts()

        return binding.root
    }

    private fun loadUserInfo() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                val username = document.getString("username") ?: "User"
                val avatarUrl = document.getString("avatar") ?: ""

                val followingList = document.get("followingList") as? List<*>
                val followerList = document.get("followerList") as? List<*>

                binding.userName.text = username
                binding.followingCount.text = "${followingList?.size ?: 0}\nFollowing"
                binding.followerCount.text = "${followerList?.size ?: 0}\nFollowers"

                if (avatarUrl.isNotEmpty()) {
                    Glide.with(this).load(avatarUrl).into(binding.userAvatar)
                }
            }
    }

    private fun loadUserPosts() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("posts")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { result ->
                val posts = result.mapNotNull { doc ->
                    doc.toObject(PostModel::class.java)
                }
                binding.recyclerMyPosts.adapter = PostAdapter(posts)
            }
            .addOnFailureListener {
                //
            }
    }
}
