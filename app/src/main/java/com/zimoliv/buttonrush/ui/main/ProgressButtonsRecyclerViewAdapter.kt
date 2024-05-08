package com.zimoliv.buttonrush.ui.main

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zimoliv.buttonrush.R
import com.zimoliv.buttonrush.database.data.MutableInt

class ProgressButtonsRecyclerViewAdapter(private val number: MutableInt, private val context2: Context): RecyclerView.Adapter<ProgressButtonsRecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val constraint: ConstraintLayout = itemView.findViewById(R.id.constraint_margin)
        val textLevel: TextView = constraint.findViewById(R.id.textViewProgress_item)
        val barProgress: ProgressBar = constraint.findViewById(R.id.progressBar_item)
        val imgButton : ImageView =  constraint.findViewById(R.id.button_img_center)
        val imageView : ImageView = itemView.findViewById(R.id.beautiful_img)
        val imageTop : ImageView = constraint.findViewById(R.id.image_top)
        val imageBottom : ImageView = constraint.findViewById(R.id.image_bottom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_progress_buttons, parent, false)
        return ViewHolder(viewItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentNightMode = context2.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            holder.imageTop.setColorFilter(ContextCompat.getColor(context2, R.color.white))
            holder.imageBottom.setColorFilter(ContextCompat.getColor(context2, R.color.white))
        } else {
            holder.imageTop.clearColorFilter()
            holder.imageBottom.clearColorFilter()
        }

        val int = number.value
        when (position) {
            0 -> {
                holder.constraint.visibility = View.INVISIBLE
                holder.imageView.visibility = View.GONE
            }
            1 -> {
                holder.imgButton.setImageResource(R.drawable.button_png)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "0"
                holder.barProgress.max = 100
                holder.barProgress.progress = int
                holder.imageView.visibility = View.GONE
            }
            2 -> {
                holder.imgButton.setImageResource(R.drawable.click_button_2_clicked)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "100"
                if (int > 100) {
                    holder.barProgress.max = 1000 - 100
                    holder.barProgress.progress = int - 100
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            3 -> {
                holder.imgButton.setImageResource(R.drawable.click_3)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "1 000"
                if (int > 1000) {
                    holder.barProgress.max = 2500 - 1000
                    holder.barProgress.progress = int - 1000
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            4 -> {
                holder.imgButton.setImageResource(R.drawable.button_two_five_k)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "2 500"
                if (int > 2500) {
                    holder.barProgress.max = 5000 - 2500
                    holder.barProgress.progress = int - 2500
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            5 -> {
                holder.imgButton.setImageResource(R.drawable.fire_button)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "5 000"
                if (int > 5000) {
                    holder.barProgress.max = 10000 - 5000
                    holder.barProgress.progress = int - 5000
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            6 -> {
                holder.imgButton.setImageResource(R.drawable.blue_dark_knight)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "10 000"
                if (int > 10000) {
                    holder.barProgress.max = 15000 - 10000
                    holder.barProgress.progress = int - 10000
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            7 -> {
                holder.imgButton.setImageResource(R.drawable.earth_button_one)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "15 000"
                if (int > 15000) {
                    holder.barProgress.max = 20000 - 15000
                    holder.barProgress.progress = int - 15000
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            8 -> {
                holder.imgButton.setImageResource(R.drawable.earth_two)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "20 000"
                if (int > 20000) {
                    holder.barProgress.max = 30000 - 20000
                    holder.barProgress.progress = int - 20000
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            9 -> {
                holder.imgButton.setImageResource(R.drawable.knight_one)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "30 000"
                if (int > 30000) {
                    holder.barProgress.max = 50000 - 30000
                    holder.barProgress.progress = int - 30000
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            10 -> {
                holder.imgButton.setImageResource(R.drawable.knight_two)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "50 000"
                if (int > 50000) {
                    holder.barProgress.max = 100000 - 50000
                    holder.barProgress.progress = int - 50000
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            11 -> {
                holder.imgButton.setImageResource(R.drawable.gold_sword)
                holder.constraint.visibility = View.VISIBLE
                holder.textLevel.text = "100 000"
                if (int > 100000) {
                    holder.barProgress.max = 1000
                    holder.barProgress.progress = int % 1000
                    removeGrayScale(holder.imgButton)
                } else {
                    holder.barProgress.progress = 0
                    applyGrayScale(holder.imgButton)
                }
                holder.imageView.visibility = View.GONE
            }
            12 -> {
                holder.constraint.visibility = View.INVISIBLE
                holder.imageView.visibility = View.VISIBLE
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    holder.imageView.setImageResource(R.drawable.img_end_ia)
                } else {
                    holder.imageView.setImageResource(R.drawable.img_start_ia)
                }
            }
        }
    }

    override fun getItemCount(): Int = 11 + 2

    private fun applyGrayScale(imageView: ImageView) {
        val color = Color.argb(150, 50, 50, 50) // Couleur sombre semi-transparente
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }

    private fun removeGrayScale(imageView: ImageView) {
        imageView.colorFilter = null
    }
}