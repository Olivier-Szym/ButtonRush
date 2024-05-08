package com.zimoliv.buttonrush.domain.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.EmojiCompatConfigurationView
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vdurmont.emoji.EmojiManager
import com.zimoliv.buttonrush.R
import java.util.Locale

class PseudoDialog(context: Context) : DialogFragment() {

    val context2 = context
    var alertDialog: AlertDialog? = null
    var selectedCountry: String = ""

    interface CreateUserDialogListener {
        fun onDialogPositiveClick(pseudo: String)
    }

    var listener: CreateUserDialogListener? = null

    fun marginFunction(left: Int, top: Int, right:Int, bottom:Int) : LinearLayout.LayoutParams {
        val marginParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        marginParams.setMargins(left, top, right, bottom)
        return marginParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context2)

        val input = EditText(context2)
        with(input) {
            inputType = InputType.TYPE_CLASS_TEXT
            hint = getString(R.string.write_your_surname)
        }

// Récupérer la liste de tous les pays
//        val locales = Locale.getAvailableLocales()
//        val countries = mutableListOf<String>()
//        for (locale in locales) {
//            val country = locale.displayCountry
//            if (country.isNotEmpty() && !countries.contains(country)) {
//                countries.add(country)
//            }
//        }
//
//        countries.sort()

        val locales = Locale.getISOCountries()

// Initialisez une liste pour stocker les noms de pays avec emojis
        val countriesWithEmojis = mutableListOf<String>()
        val countries = mutableListOf<String>()

// Pour chaque code ISO de pays
        for (countryCode in locales) {
            println("test1:$countryCode")
            // Créez un objet Locale avec le code de pays
            val locale = Locale("", countryCode)

            // Vérifiez si le nom du pays contient uniquement des lettres
            val countryName = locale.country
            if (countryName.isNotEmpty() && countryName.all { it.isLetter() }) {
                countries.add(countryName)
                println("test2:$countryName")
                // Obtenez l'emoji correspondant au pays
                val emoji = EmojiManager.getForAlias(countryCode.toLowerCase())

                // Ajoutez l'emoji et le nom du pays à la liste
                if (emoji != null) {
                    countriesWithEmojis.add("${emoji.unicode} $countryName")
                } else {
                    // Si aucun emoji n'est trouvé, ajoutez simplement le nom du pays
                    countriesWithEmojis.add(countryName)
                }
            }
        }

// Triez la liste des pays avec emojis par ordre alphabétique
        countriesWithEmojis.sortWith(CountryComparator())
        countries.sort()

// Récupérer le pays par défaut du téléphone
        val defaultCountry = Locale.getDefault().displayCountry
        val defaultCountryIndex = countries.indexOf(defaultCountry)

        val spinner = Spinner(context2)
        val adapter = ArrayAdapter(context2, android.R.layout.simple_spinner_item, countriesWithEmojis)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

// Définir l'index du pays par défaut
        if (defaultCountryIndex != -1) {
            spinner.setSelection(defaultCountryIndex)
        }

        val textView2 = TextView(context2)
        textView2.text = "Select your team :"
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
//        textView2.gravity = Gravity.CENTER

        val layout = LinearLayout(context2)
        layout.orientation = LinearLayout.VERTICAL
        layout.gravity = Gravity.CENTER
        layout.addView(input, marginFunction(30, 0, 30, 0))
        layout.addView(textView2, marginFunction(30, 20, 30, 0))
        layout.addView(spinner, marginFunction(0, 12, 0, 0))

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val layoutParams = spinner.layoutParams as LinearLayout.LayoutParams
                layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                layoutParams.gravity = Gravity.CENTER
                spinner.layoutParams = layoutParams
                // Récupérez le pays sélectionné
                selectedCountry = countriesWithEmojis[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Gérez le cas où rien n'est sélectionné (si nécessaire)
            }
        }


        builder.setCancelable(false)

        builder.setTitle(getString(R.string.it_is_now))
            .setView(layout)
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

class CountryComparator : Comparator<String> {
    override fun compare(country1: String, country2: String): Int {
        // Supprimez les emojis des noms de pays pour la comparaison
        val countryName1 = country1.substringAfter(" ")
        val countryName2 = country2.substringAfter(" ")

        // Utilisez la méthode de comparaison de chaînes par défaut pour trier les noms de pays
        return countryName1.compareTo(countryName2)
    }
}