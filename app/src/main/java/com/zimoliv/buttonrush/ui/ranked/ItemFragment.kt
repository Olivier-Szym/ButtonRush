package com.zimoliv.buttonrush.ui.ranked

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zimoliv.buttonrush.MainActivity2
import com.zimoliv.buttonrush.R
import com.zimoliv.buttonrush.databinding.FragmentItemListBinding

class ItemFragment : Fragment(), MyItemRecyclerViewAdapter.UserItemListener {

    private var onTime = false
    private var idMode = "career"//getString(R.string.career_id)
    private lateinit var listUser: MutableList<UserItem>
    private lateinit var listFriends: MutableList<String>
    private var surname = ""
    private var career = true

    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyItemRecyclerViewAdapter

    private lateinit var recyclerView : RecyclerView

    private var favorite = false

    private lateinit var searchView: SearchView

    private val filteredList = mutableListOf<UserItem>()
    private val copyListAll = mutableListOf<UserItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        arguments?.let {
            idMode = it.getString("string1") ?: getString(R.string.career_id)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        listUser = mutableListOf()
        listFriends = mutableListOf()

        searchView = binding.searchView
        searchView.isIconified = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Vous pouvez ignorer cette méthode, car elle est appelée lors de la soumission de la recherche
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        val listRaw= (activity as MainActivity2).getSaveFriends()
        surname = (activity as MainActivity2).getSaveName()

        listFriends.clear()
        listFriends.addAll(listRaw)

        career = idMode == getString(R.string.career_id)

        adapter = MyItemRecyclerViewAdapter(listUser, surname, this, listFriends, career, requireContext())
        recyclerView = binding.list
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter


        getUserFirebase()

        binding.floatingActionButton.setOnClickListener {
            goToUser()
        }

        val activityMain = (activity as MainActivity2)
        val actionBar = activityMain.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val mode = when(idMode) {
            getString(R.string.career_id) -> {
                getString(R.string.career)
            }
            getString(R.string.cent_id) -> {
                "100"
            }
            getString(R.string.cinq_id) -> {
                "500"
            }
            getString(R.string.k_id)-> {
                getString(R.string.one_k)
            }
            getString(R.string.dix_id) -> {
                getString(R.string.ten_k)
            }
            getString(R.string.marath_id) -> {
                getString(R.string.marathon)
            }
            else -> {""}
        }
        if (mode.isNotEmpty()) {
            activityMain.supportActionBar?.title = getString(R.string.ranking_mode_title, mode)
        } else {
            activityMain.supportActionBar?.title = getString(R.string.ranking_default_title)
        }

        return root
    }

    private fun filterList(query: String?) {

        filteredList.clear()

        if (query.isNullOrBlank()) {
            listUser.clear()
            listUser.addAll(copyListAll)
        } else {
            val copyList = copyListAll
            filteredList.addAll(copyList.filter { it.pseudo.contains(query, ignoreCase = true) })

            listUser.clear()
            listUser.addAll(filteredList)
        }
        adapter.notifyDataSetChanged()
    }

    private fun goToUser() {
        val userToScrollTo = surname

        val positionToScroll = listUser.indexOfFirst { it.pseudo == userToScrollTo }

        if (positionToScroll != -1) {
            // Si l'utilisateur est trouvé, faites défiler le RecyclerView jusqu'à cette position
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            layoutManager.scrollToPositionWithOffset(positionToScroll, 0)
        }
    }

    private fun getUserFirebase() {
        val database = FirebaseDatabase.getInstance().reference

        database.child("utilisateurs").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val leaderboardList = mutableListOf<UserItem>()
                for (userSnapshot in snapshot.children) {
                    val pseudo = userSnapshot.key ?: ""
                    val score = userSnapshot.child(idMode).getValue(Int::class.java) ?: 0
                    val trending = userSnapshot.child("${idMode}_trending").getValue(Int::class.java) ?: 0
                    leaderboardList.add(UserItem(pseudo, score, trending))
                }

                updateList(leaderboardList)
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }

        })
    }

    fun updateList(leaderboardList :  MutableList<UserItem>) {
        listUser.clear()

//        leaderboardList.forEach { (username, score) ->
//            val userData = UserItem(username, score)
//            listUser.add(userData)
//        }
        listUser.addAll(leaderboardList)

        if (career) {
            listUser.sortByDescending { it.number }
        } else {
            listUser.sortWith(compareBy<UserItem> { it.number == 0 }.thenBy { it.number })
        }

        adapter.notifyDataSetChanged()
        copyListAll.clear()
        copyListAll.addAll(listUser)

        if (!onTime) {
            goToUser()
            onTime = true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_item, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val activitemain = (activity as MainActivity2)
        val actionBar = activitemain.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(false)
        activitemain.supportActionBar?.title = getString(R.string.app_name)
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {

                findNavController().popBackStack()
                true
            }
            R.id.action_star -> {
                if (!favorite) {
                    val mutable = mutableListOf<UserItem>()
                    for (user in listUser) {
                        val estEgaleAUnItem = listFriends.any { it == user.pseudo }

                        if (estEgaleAUnItem) {
                            mutable.add(user)
                        }

                        if (user.pseudo == surname) {
                            mutable.add(user)
                        }
                    }
                    if (career) {
                        listUser.sortByDescending { it.number }
                    } else {
                        listUser.sortWith(compareBy<UserItem> { it.number == 0 }.thenBy { it.number })
                    }

                    listUser.clear()
                    listUser.addAll(mutable)

                    favorite = true

                    adapter.notifyDataSetChanged()
                } else {
                    favorite = false
                    getUserFirebase()
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAddFriend(user: Int) {
        val userIndex = listUser[user]
        val mutable : MutableList<String> = mutableListOf()
        val list = (activity as MainActivity2).getSaveFriends()

        var delete = false

        for (friend in list) {
            if (friend != userIndex.pseudo) {
                mutable.add(friend)
            } else {
                delete = true
            }
        }

        if (!delete) {
            mutable.add(userIndex.pseudo)
            listFriends.add(userIndex.pseudo)
        } else {
            mutable.remove(userIndex.pseudo)
            listFriends.remove(userIndex.pseudo)
        }

        (activity as MainActivity2).setSaveFriends(mutable.toList())
    }
}