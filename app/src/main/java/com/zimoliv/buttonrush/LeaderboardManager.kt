package com.zimoliv.buttonrush

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class LeaderboardManager(private val database: DatabaseReference) {

    fun getLeaderboard(category: String, callback: (List<Pair<String, Int>>) -> Unit) {
        val leaderboardList = mutableListOf<Pair<String, Int>>()

        database.child("utilisateurs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val pseudo = userSnapshot.key ?: ""
                    val score = userSnapshot.child(category).getValue(Int::class.java) ?: 0
                    leaderboardList.add(Pair(pseudo, score))
                }

                // Trier la liste selon le score
                leaderboardList.sortByDescending { it.second }

                callback(leaderboardList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gérer les erreurs
            }
        })
    }
}
