package com.zimoliv.buttonrush

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zimoliv.buttonrush.databinding.ActivityMain2Binding
import com.zimoliv.buttonrush.ui.home.PauseDialogListener
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    private lateinit var navView: BottomNavigationView

    private var dialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when (this.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.marron))
            }
            Configuration.UI_MODE_NIGHT_NO -> {}
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {}
        }

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main2)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        checkOnlineAccorded()
    }

    private fun checkOnlineAccorded() {
        val name = getSaveName()
        if (name != "User") {
            val database = Firebase.database
            val myRef = database.getReference("utilisateurs").child(name)
            myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {

                        val record100 = getRecord100()
                        if ((dataSnapshot.child("100click").getValue(Int::class.java)?.toLong()
                                ?: 0) > record100
                        ) {
                            val utilisateurData = HashMap<String, Any>().apply {
                                put("100click", record100)
                            }
                            myRef.updateChildren(utilisateurData)
                        }

                        val record500 = getRecord500()
                        if ((dataSnapshot.child("500click").getValue(Int::class.java)?.toLong()
                                ?: 0) > record500
                        ) {
                            val utilisateurData = HashMap<String, Any>().apply {
                                put("500click", record500)
                            }
                            myRef.updateChildren(utilisateurData)
                        }

                        val record1k = getRecord1k()
                        if ((dataSnapshot.child("1kclick").getValue(Int::class.java)?.toLong()
                                ?: 0) > record1k
                        ) {
                            val utilisateurData = HashMap<String, Any>().apply {
                                put("1kclick", record1k)
                            }
                            myRef.updateChildren(utilisateurData)
                        }

                        val record10k = getRecord10k()
                        if ((dataSnapshot.child("10kclick").getValue(Int::class.java)?.toLong()
                                ?: 0) > record10k
                        ) {
                            val utilisateurData = HashMap<String, Any>().apply {
                                put("10kclick", record10k)
                            }
                            myRef.updateChildren(utilisateurData)
                        }

                        val recordMarathon = getRecordMarathon()
                        if ((dataSnapshot.child("marathon").getValue(Int::class.java)?.toLong()
                                ?: 0) > recordMarathon
                        ) {
                            val utilisateurData = HashMap<String, Any>().apply {
                                put("marathon", recordMarathon)
                            }
                            myRef.updateChildren(utilisateurData)
                        }

                    } else {
                        // Le nœud n'existe pas
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Gestion des erreurs
                }
            })
        }
    }

    fun setNavigationEnabled(isEnabled: Boolean) {
        navView.menu.findItem(R.id.navigation_home).setOnMenuItemClickListener {
            if (isEnabled) {
                false
            } else {
                Toast.makeText(this, getString(R.string.save_before), Toast.LENGTH_SHORT).show()
                true
            }
        }
    }
    fun setSaveNumber(number: Int) {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        number.let { sharedPreferences.edit().putInt("number", it).apply() }
    }
    fun getSaveNumber(): Int {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("number", 0)
    }
    fun getSaveName() : String {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "User")
        return name ?: "User"
    }
    fun setSaveName(name: String) {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        name.let { sharedPreferences.edit().putString("name", it).apply() }
    }
    fun getSaveFriends() : List<String> {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("friend", "")
        if (name != null) {
            return name.split("¤")
        }
        return listOf()
    }
    fun setSaveFriends(list: List<String>) {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)

        var bool = true
        var str = ""

        for (friend in list) {
            if (bool) {
                str = friend
                bool = false
            } else
                str = str + "¤" + friend
        }

        str.let { sharedPreferences.edit().putString("friend", it).apply() }
    }
    fun getNumberClick(): Int {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("intclick", -2)
    }
    fun getGoal(): Int {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("goalclick", 0)
    }
    fun setGoal(int: Int) {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        int.let { sharedPreferences.edit().putInt("goalclick", it).apply() }
    }
    fun getTime(): Long {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("longtime", 0)
    }
    fun setTime(long: Long) {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        long.let { sharedPreferences.edit().putLong("longtime", it).apply() }
    }
    fun setCounterClick(int: Int) {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        int.let { sharedPreferences.edit().putInt("intclick", it).apply() }
    }
    fun showPauseDialog(time: String, clicks : String,listener: PauseDialogListener) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pause, null)
        dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.buttonResume).setOnClickListener {
            listener.onResumeClicked()
            dialog?.dismiss()
        }

        dialogView.findViewById<Button>(R.id.buttonStop).setOnClickListener {
            listener.onStopClicked()
            setCounterClick(-1)
            setTime(0)
            dialog?.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.textViewTime).text = time
        dialogView.findViewById<TextView>(R.id.textViewClicks).text = clicks

        dialog?.show()
    }
    fun putKonfettis() {
        val konfettiView = binding.konfettiViewDialog

        konfettiView.bringToFront()
        konfettiView.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE)
            .setDirection(0.0, 359.0)
            .setSpeed(3f, 6f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(0f, konfettiView.width.toFloat(), -50f, 0f) // Partie supérieure de l'écran
            .streamFor(500, 2000)
    }
    fun findRecords(goal: Int) : Long {
        return when (goal) {
            500 -> {
                getRecord500()
            }
            1000 -> {
                getRecord1k()
            }
            10000 -> {
                getRecord10k()
            }
            42000 -> {
                getRecordMarathon()
            }
            else -> {
                //100 clicks goal par défault
                getRecord100()
            }
        }
    }
    fun setRecord100(long: Long) {
        setRecord(long, "100c")
    }
    private fun getRecord100() : Long {
        return getRecord("100c")
    }
    fun setRecord500(long: Long) {
        setRecord(long, "500c")
    }
    private fun getRecord500() : Long {
        return getRecord("500c")
    }
    fun setRecord1k(long: Long) {
        setRecord(long, "1k")
    }
    private fun getRecord1k() : Long {
        return getRecord("1k")
    }
    fun setRecord10k(long: Long) {
        setRecord(long, "10k")
    }
    private fun getRecord10k() : Long {
        return getRecord("10k")
    }
    fun setRecordMarathon(long: Long) {
        setRecord(long, "42k")
    }
    private fun getRecordMarathon() : Long {
        return getRecord("42k")
    }
    private fun getRecord(id: String): Long {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(id, 0)
    }
    private fun setRecord(long: Long, id: String) {
        val sharedPreferences = this.getSharedPreferences("app.buttonrush", Context.MODE_PRIVATE)
        long.let { sharedPreferences.edit().putLong(id, it).apply() }
    }
}