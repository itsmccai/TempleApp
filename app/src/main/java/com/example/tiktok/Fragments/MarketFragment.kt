package com.example.tiktok

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktok.Model.MarketModel
import com.example.tiktok.LatestMarketAdapter
import com.example.tiktok.MarketAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MarketFragment : Fragment() {

    private lateinit var searchBar: EditText
    private lateinit var marketRecyclerView: RecyclerView
    private lateinit var marketAdapter: MarketAdapter
    private lateinit var latestRecyclerView: RecyclerView
    private lateinit var latestAdapter: LatestMarketAdapter

    private val db = FirebaseFirestore.getInstance()
    private var fullProductList: List<MarketModel> = emptyList()

    // auto scroll setup
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val autoScrollSpeed: Long = 30L * 100
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val lm = latestRecyclerView.layoutManager as LinearLayoutManager
            val lastVisibleItem = lm.findLastCompletelyVisibleItemPosition()
            val itemCount = latestAdapter.itemCount

            val nextPosition = if (lastVisibleItem < itemCount - 1) {
                lastVisibleItem + 1
            } else {
                0
            }

            latestRecyclerView.smoothScrollToPosition(nextPosition)
            autoScrollHandler.postDelayed(this, autoScrollSpeed)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_market, container, false)

        // setup search bar
        searchBar = view.findViewById(R.id.search_bar)

        // setup latest horizontal items
        latestRecyclerView = view.findViewById(R.id.latestRecyclerView)
        latestRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val latestDummy = listOf(
            MarketModel(
                title = "Latest Jacket",
                caption = "Warm and cozy",
                price = 3200.0,
                isProduct = true
            ),
            MarketModel(
                title = "Limited Sneakers",
                caption = "Rare edition",
                price = 5300.0,
                isProduct = true
            ),
            MarketModel(
                title = "Bape Hoodie",
                caption = "Brand New",
                imageUrl = "d",
                price = 3000.0,
                isProduct = true
            ),
            MarketModel(
                title = "Gaming Keyboard",
                caption = "RGB lighting",
                price = 4900.0,
                isProduct = true
            )

        )


        latestAdapter = LatestMarketAdapter(latestDummy)
        latestRecyclerView.adapter = latestAdapter

        autoScrollHandler.postDelayed(autoScrollRunnable, autoScrollSpeed)

        // setup market vertical list
        marketRecyclerView = view.findViewById(R.id.marketRecyclerView)
        marketRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        db.collection("products")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                fullProductList = snapshot.mapNotNull { doc ->
                    doc.toObject(MarketModel::class.java)
                }.filter { it.isProduct }

                marketAdapter = MarketAdapter(fullProductList)
                marketRecyclerView.adapter = marketAdapter
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load products: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        // search bar filter logic
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                val filteredList = fullProductList.filter {
                    it.title.lowercase().contains(query)
                }
                marketAdapter.updateList(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }
}
