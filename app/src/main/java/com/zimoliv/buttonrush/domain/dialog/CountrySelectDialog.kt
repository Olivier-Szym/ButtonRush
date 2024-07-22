package com.zimoliv.buttonrush.domain.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.vdurmont.emoji.EmojiManager
import com.zimoliv.buttonrush.R
import java.util.Locale

class CountrySelectDialog(private val selectedCountry: String) : DialogFragment() {

    interface CountryDialogListener {
        fun onCountrySelected(country: String)
    }

    var listener: CountryDialogListener? = null
    private var lastCountry: String = "FR"

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        listener = try {
//            context as CountryDialogListener
//        } catch (e: ClassCastException) {
//            throw ClassCastException("$context must implement CountryDialogListener")
//        }
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_country, null)

        val spinner: Spinner = dialogView.findViewById(R.id.spinner_countries2)
        val buttonAccept: Button = dialogView.findViewById(R.id.button_accept)
        val buttonCancel: Button = dialogView.findViewById(R.id.button_cancel)

        // Get list of countries
        val locales = Locale.getISOCountries()
        val countries = locales.map { Pair(Locale("", it).displayCountry, it) }.sortedBy { it.first }
//        val countries_with_emoji = countries.map { "${EmojiManager.getForAlias(it.lowercase(Locale.getDefault())).unicode} $it" }

// Liste pour stocker les noms des pays avec les emojis
        val countries_with_emoji = mutableListOf<String>()

        for (countryCode in locales) {
            val locale = Locale("", countryCode)
            val countryName = locale.displayCountry

            if (countryName.isNotEmpty()) {
                // Obtenez l'emoji correspondant au code du pays
                val emoji = EmojiManager.getForAlias(countryCode.lowercase(Locale.getDefault()))

                if (emoji != null) {
                    // Ajoutez l'emoji et le nom du pays à la liste
                    countries_with_emoji.add("${emoji.unicode} $countryName")
                } else {
                    // Si aucun emoji n'est trouvé, ajoutez simplement le nom du pays
                    countries_with_emoji.add(countryName)
                }
            }
        }

        countries_with_emoji.sortWith(CountryComparator())

        // Set up spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, countries_with_emoji)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set default selection
        val foundCountry = countries.find { it.first == selectedCountry }
        val defaultCountryIndex = countries.indexOf(foundCountry)
        if (defaultCountryIndex != -1) {
            spinner.setSelection(defaultCountryIndex)
        }

        // Handle spinner item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                lastCountry = countries[position].second
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        buttonAccept.setOnClickListener {
//            val selectedCountry = spinner.selectedItem.toString()
//            val selectedCode = countries.find { it.first == selectedCountry.substring(2) }
//            println(countries)
//            println(selectedCountry.substring(2))
//            println(selectedCode)
//            selectedCode?.let { it1 -> listener?.onCountrySelected(it1.second) }

            listener?.onCountrySelected(lastCountry)
            dismiss()
        }

        buttonCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(dialogView)
        builder.setTitle(getString(R.string.select_your_team))
        return builder.create()
    }

//    override fun onDetach() {
//        super.onDetach()
//        listener = null
//    }
}
