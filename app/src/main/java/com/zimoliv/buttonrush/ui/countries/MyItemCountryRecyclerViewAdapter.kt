package com.zimoliv.buttonrush.ui.countries

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.vdurmont.emoji.EmojiManager
import com.zimoliv.buttonrush.R
import java.util.Locale

class MyItemCountryRecyclerViewAdapter(
    private val countries: List<CountryData>, private val context2: Context, private val countryUser: String
) : RecyclerView.Adapter<MyItemCountryRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CircularRevealCardView = itemView.findViewById(R.id.card_view_country)
        val colorConstraint1 : ConstraintLayout = cardView.findViewById(R.id.constraint_color1_country)
        val colorConstraint2 : ConstraintLayout = cardView.findViewById(R.id.constraint_color2_country)
        val ranke: TextView = cardView.findViewById(R.id.item_number_country)
        val pseudoView: TextView = cardView.findViewById(R.id.content_text_view_country)
        val rankeText: TextView = cardView.findViewById(R.id.ranke_text_country)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_countries, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val country = countries[position]
        holder.ranke.text = formatNumberWithSpaces(country.number)
        holder.rankeText.text = formatNumberWithSpaces(position + 1)

        val locale = Locale("", country.name)
        val countryName = locale.displayCountry
        val emoji = EmojiManager.getForAlias(country.name.lowercase(Locale.getDefault()))
        if (emoji != null) {
            holder.pseudoView.text = "${emoji.unicode} $countryName"
        } else {
            holder.pseudoView.text = countryName
        }

        updateColorConstraint(holder, position, itemCount)
        val isCurrentUser = countryUser == country.name
        updateBackgroundColor(holder, isCurrentUser)
    }

    override fun getItemCount(): Int = countries.size

    private fun formatNumberWithSpaces(number: Int): String {
        val numberString = number.toString()
        val length = numberString.length

        val result = StringBuilder()
        for (i in 0 until length) {
            result.append(numberString[i])
            if ((length - i) % 3 == 1 && i < length - 1) {
                result.append(' ')
            }
        }

        return result.toString()
    }

    private fun updateBackgroundColor(holder: ViewHolder, isCurrentUser: Boolean) {
        if (isCurrentUser) {
            val currentNightMode = context2.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                holder.colorConstraint1.setBackgroundColor(ContextCompat.getColor(context2, R.color.white_dark))
            } else {
                holder.colorConstraint1.setBackgroundColor(ContextCompat.getColor(context2, R.color.black))
            }
        }
    }

    private fun updateColorConstraint(holder: ViewHolder, position: Int, itemCount: Int) {
        val quarter = itemCount / 4
        when {
            position < quarter -> {
                holder.colorConstraint2.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_orange))
                holder.colorConstraint1.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_orange))
                holder.rankeText.setBackgroundColor(ContextCompat.getColor(context2, R.color.red_orange))
            }
            position < quarter * 2 -> {
                holder.colorConstraint2.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_red))
                holder.colorConstraint1.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_red))
                holder.rankeText.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_reder))
            }
            position < quarter * 3 -> {
                holder.colorConstraint2.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_blue))
                holder.colorConstraint1.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_blue))
                holder.rankeText.setBackgroundColor(ContextCompat.getColor(context2, R.color.colorPrimaryDark))
            }
            else -> {
                holder.colorConstraint2.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_green))
                holder.colorConstraint1.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_green))
                holder.rankeText.setBackgroundColor(ContextCompat.getColor(context2, R.color.adapter_greener))
            }
        }
    }

}