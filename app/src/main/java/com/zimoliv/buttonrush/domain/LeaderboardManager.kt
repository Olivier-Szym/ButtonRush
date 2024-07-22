package com.zimoliv.buttonrush.domain

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.zimoliv.buttonrush.ui.ranked.UserItem

class LeaderboardManager(private val database: DatabaseReference) {

    fun getLeaderboard(category: String, callback: (List<UserItem>) -> Unit) {
        val leaderboardList = mutableListOf<UserItem>()

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val pseudo = userSnapshot.key ?: ""
                    val score = userSnapshot.child(category).getValue(Int::class.java) ?: 0
                    val trending = userSnapshot.child("${category}_trending").getValue(Int::class.java) ?: 0
                    val country = userSnapshot.child("country").getValue(String::class.java) ?: ""
                    leaderboardList.add(UserItem(pseudo, score, trending, country))
                }

                // Trier la liste selon le score
                leaderboardList.sortByDescending { it.number }

                callback(leaderboardList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gérer les erreurs
            }
        })
    }
}
