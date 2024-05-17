package com.zimoliv.buttonrush.ui.ranked

import android.content.Context
import android.content.res.Configuration
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.google.android.material.circularreveal.cardview.CircularRevealCardView
import com.vdurmont.emoji.EmojiManager
import com.zimoliv.buttonrush.R
import java.util.Locale

class MyItemRecyclerViewAdapter(
    private val users: MutableList<UserItem>,
    private val pseudo: String,
    private val itemClickListener: UserItemListener,
    private val listFriends : MutableList<String>,
    private val career: Boolean,
    private val context2: Context
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    interface UserItemListener {
        fun onAddFriend(user: Int)
    }

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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: CircularRevealCardView = itemView.findViewById(R.id.card_view)
        val colorConstraint1 : ConstraintLayout = cardView.findViewById(R.id.constraint_color1)
        val colorConstraint2 : ConstraintLayout = cardView.findViewById(R.id.constraint_color2)
        val ranke: TextView = cardView.findViewById(R.id.item_number)
        val pseudoView: TextView = cardView.findViewById(R.id.content_text_view)
        val starImage: ImageView = cardView.findViewById(R.id.image_star)
        val rankeText: TextView = cardView.findViewById(R.id.ranke_text)
        val imgButton : ImageView =  cardView.findViewById(R.id.img_button)
        val imgTrend : ImageView =  cardView.findViewById(R.id.image_trending)
        val flagText: TextView = cardView.findViewById(R.id.text_view_emoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]

        holder.pseudoView.text = user.pseudo
        holder.rankeText.text = formatNumberWithSpaces(position + 1)

        val locale = Locale("", user.country)
        val countryName = locale.displayCountry
        if (countryName.isNotEmpty()) {
            val emoji = EmojiManager.getForAlias(user.country.lowercase(Locale.getDefault()))
            if (emoji != null) {
                holder.flagText.text = emoji.unicode
            }
        }

        when (user.trending) {
            -1 -> {
                holder.imgTrend.setImageResource(R.drawable.baseline_trending_down_24)
            }
            1 -> {
                holder.imgTrend.setImageResource(R.drawable.baseline_trending_up_24)
            }
            else -> {
                holder.imgTrend.setImageDrawable(null)
            }
        }

        if (career) {
            holder.ranke.text = formatNumberWithSpaces(user.number)
            setImgButton(holder, user.number)
        } else {
            if (user.number != 0) {
                holder.ranke.text = formatTime(user.number)
            } else {
                holder.ranke.text = "--:--"
            }
        }

        updateColorConstraint(holder, position, itemCount)
        val isCurrentUser = user.pseudo == pseudo
        updateBackgroundColor(holder, isCurrentUser)

        updateStarIcon(holder, user)

        holder.starImage.setOnClickListener {
            if (listFriends.contains(user.pseudo)) {
                listFriends.remove(user.pseudo)
                holder.starImage.setImageResource(R.drawable.ic_baseline_star_outline_24)
            } else {
                listFriends.add(user.pseudo)
                holder.starImage.setImageResource(R.drawable.ic_baseline_star_rate_24)
            }
            itemClickListener.onAddFriend(position)
        }
    }

    private fun setImgButton(holder: ViewHolder, number: Int) {
        holder.imgButton.visibility = View.VISIBLE
        if (number < 100) {
            holder.imgButton.setImageResource(R.drawable.button_png)
        } else if (number < 1000) {
            holder.imgButton.setImageResource(R.drawable.click_button_2_clicked)
        } else if (number < 2500) {
            holder.imgButton.setImageResource(R.drawable.click_3)
        } else if (number < 5000) {
            holder.imgButton.setImageResource(R.drawable.button_two_five_k)
        } else if (number < 10000) {
            holder.imgButton.setImageResource(R.drawable.fire_button)
        } else if (number < 15000) {
            holder.imgButton.setImageResource(R.drawable.blue_dark_knight)
        } else if (number < 20000) {
            holder.imgButton.setImageResource(R.drawable.earth_button_one)
        } else if (number < 30000) {
            holder.imgButton.setImageResource(R.drawable.earth_two)
        } else if (number < 50000) {
            holder.imgButton.setImageResource(R.drawable.knight_one)
        } else if (number < 100000) {
            holder.imgButton.setImageResource(R.drawable.knight_two)
        } else {
            holder.imgButton.setImageResource(R.drawable.gold_sword)
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

    private fun updateBackgroundColor(holder: ViewHolder, isCurrentUser: Boolean) {
        if (isCurrentUser) {
            val currentNightMode = context2.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                holder.colorConstraint1.setBackgroundColor(ContextCompat.getColor(context2, R.color.white_dark))
            } else {
                holder.colorConstraint1.setBackgroundColor(ContextCompat.getColor(context2, R.color.black))
            }
            holder.starImage.visibility = View.GONE
        } else {
            holder.starImage.visibility = View.VISIBLE
        }
    }

    private fun formatTime(milliseconds: Int): String {
        val hours = (milliseconds / (1000 * 60 * 60))
        val minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60))
        val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000)
        val millis = (milliseconds % 1000)

        val formattedTime = StringBuilder()

        if (hours > 0) {
            formattedTime.append("$hours h ")
        }
        if (minutes > 0) {
            formattedTime.append("$minutes min ")
        }
        if (seconds > 0) {
            formattedTime.append("$seconds s ")
        }
        if (millis > 0) {
            formattedTime.append("$millis ms ")
        }

        return formattedTime.toString().trim()
    }

    private fun updateStarIcon(holder: ViewHolder, user: UserItem) {
        if (listFriends.contains(user.pseudo)) {
            holder.starImage.setImageResource(R.drawable.ic_baseline_star_rate_24)
        } else {
            holder.starImage.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
    }

    override fun getItemCount(): Int = users.size
}