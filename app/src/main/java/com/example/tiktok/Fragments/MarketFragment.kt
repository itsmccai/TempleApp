package com.example.tiktok

import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MarketFragment : Fragment() {

    private lateinit var searchBar: EditText
    private lateinit var marketRecyclerView: RecyclerView
    private lateinit var marketAdapter: MarketAdapter

    private val db = FirebaseFirestore.getInstance()
    private var fullProductList: List<MarketModel> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_market, container, false)

        // search bar and product list
        searchBar = view.findViewById(R.id.search_bar)
        marketRecyclerView = view.findViewById(R.id.marketRecyclerView)
        marketRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // load products from firebase
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
                Toast.makeText(requireContext(), "failed to load products：${it.message}", Toast.LENGTH_SHORT).show()
            }

        // listening search bar
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
}
