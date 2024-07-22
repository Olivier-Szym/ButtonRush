package com.zimoliv.buttonrush.ui.countries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zimoliv.buttonrush.MainActivity2
import com.zimoliv.buttonrush.databinding.FragmentCountriesListBinding

class CountriesFragment : Fragment() {

    private var _binding: FragmentCountriesListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyItemCountryRecyclerViewAdapter
    private lateinit var recyclerView : RecyclerView


    private lateinit var database: DatabaseReference
    private var valueEventListener: ValueEventListener? = null
    private val listCountries = mutableListOf<CountryData>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountriesListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

        val country = (activity as MainActivity2).getCountry() ?: ""

        adapter = MyItemCountryRecyclerViewAdapter(listCountries, requireContext(), country)
        recyclerView = binding.list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        database = FirebaseDatabase.getInstance().reference.child("countries")

//        getCountryFirebase()

        return root
    }

    override fun onStart() {
        super.onStart()
        getCountryFirebase()
    }

    override fun onStop() {
        super.onStop()
        // Remove listener to prevent memory leaks
        valueEventListener?.let {
            database.removeEventListener(it)
        }
    }

    private fun getCountryFirebase() {
//        val database = FirebaseDatabase.getInstance().reference
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val leaderboardList = mutableListOf<CountryData>()
                for (userSnapshot in snapshot.children) {
                    val pseudo = userSnapshot.key ?: ""
                    val score = userSnapshot.getValue(Int::class.java) ?: 0
                    leaderboardList.add(CountryData(pseudo, score))
                }
                updateList(leaderboardList)
            }
            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }
        }
//        database.child("countries").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val leaderboardList = mutableListOf<CountryData>()
//                for (userSnapshot in snapshot.children) {
//                    val pseudo = userSnapshot.key ?: ""
//                    val score = userSnapshot.getValue(Int::class.java) ?: 0
//                    leaderboardList.add(CountryData(pseudo, score))
//                }
//                updateList(leaderboardList)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                //TODO("Not yet implemented")
//            }
//        })
        valueEventListener?.let {
            database.addValueEventListener(it)
        }
    }

    fun updateList(leaderboardList :  MutableList<CountryData>) {
        listCountries.clear()
        listCountries.addAll(leaderboardList)
        listCountries.sortByDescending { it.number }
        adapter.notifyDataSetChanged()
    }
}