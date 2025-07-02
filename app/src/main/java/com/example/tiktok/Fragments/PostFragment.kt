package com.example.tiktok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.tiktok.Model.PostModel
import com.example.tiktok.databinding.FragmentPostBinding

class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostBinding.inflate(inflater, container, false)

        val samplePosts = listOf(
            PostModel(R.drawable.logo, "NO.1"),
            PostModel(R.drawable.logo, "NO.2"),
            PostModel(R.drawable.logo, "NO.3"),
            PostModel(R.drawable.logo, "You need to study ClassAction BEFORE taking this quiz. Study by working on the General Questions and reading Outlines, Images and Tables. Participate in Canvas Discussions to study together."),
            PostModel(R.drawable.logo, "NO.5")
        )
        binding.postRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.postRecyclerView.adapter = PostAdapter(samplePosts)

        return binding.root
    }
}
