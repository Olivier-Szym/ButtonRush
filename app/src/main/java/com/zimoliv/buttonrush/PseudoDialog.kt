package com.zimoliv.buttonrush

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PseudoDialog(context: Context) : DialogFragment() {

    val context2 = context
    var alertDialog: AlertDialog? = null

    interface CreateUserDialogListener {
        fun onDialogPositiveClick(pseudo: String)
    }

    var listener: CreateUserDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)

        val input = EditText(context)
        with(input) {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = getString(R.string.write_your_surname)
        }

        builder.setCancelable(false)

        builder.setTitle(getString(R.string.it_is_now))
            .setView(input)
            .setPositiveButton(getString(R.string.accept), null) // null pour désactiver la fermeture automatique

        alertDialog = builder.create()
        alertDialog?.setOnShowListener {
            // Réactivez le bouton "Valider" pour gérer manuellement la fermeture
            val positiveButton = alertDialog?.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton?.setOnClickListener {
                val pseudo = input.text.toString()
                if (pseudo.isNotEmpty()) {
                    if (pseudo.length < 20) {
                        val database = Firebase.database
                        val myRef = database.getReference("utilisateurs").child(pseudo)

                        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Toast.makeText(context2, getString(R.string.alredy_taken_pseudo), Toast.LENGTH_SHORT).show()
                                } else {
                                    listener?.onDialogPositiveClick(pseudo)
                                    alertDialog?.dismiss() // Fermer manuellement le dialogue
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Gérer les erreurs
                            }
                        })
                    } else {
                        Toast.makeText(context2, getString(R.string.twenty_minimum), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context2, getString(R.string.write_your_surname), Toast.LENGTH_LONG).show()
                }
            }
        }

        return alertDialog as Dialog
    }
}