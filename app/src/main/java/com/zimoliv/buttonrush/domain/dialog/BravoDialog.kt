package com.zimoliv.buttonrush.domain.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.zimoliv.buttonrush.R

class BravoDialog(context: Context, str: String, pseudo: String, record: Boolean, lastRecord: Long, recordLong: Long) {
    private val alertDialog: AlertDialog

    init {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.custom_dialog_layout, null)

        val pseudoTextView = dialogView.findViewById<TextView>(R.id.dialog_pseudo)
        val titleTextView = dialogView.findViewById<TextView>(R.id.dialog_title)
        val messageTextView = dialogView.findViewById<TextView>(R.id.dialog_message)
        val iconImageView = dialogView.findViewById<ImageView>(R.id.dialog_icon)
        val recordTextView = dialogView.findViewById<TextView>(R.id.your_record_view)
        val differenceTextView = dialogView.findViewById<TextView>(R.id.text_difference_record)
        pseudoTextView.text = pseudo
        messageTextView.text = str

        if (record) {
            titleTextView.text = context.getString(R.string.new_record)
            iconImageView.setImageResource(R.drawable.knight_happy)
            recordTextView.text = context.getString(R.string.your_record, formatDuration(recordLong, context))
            differenceTextView.setTextColor(ContextCompat.getColor(context, R.color.adapter_greener))
            if (lastRecord > 0) {
                differenceTextView.text = "- ${formatDuration(lastRecord - recordLong, context)}"
            } else {
                differenceTextView.text = "+ ${formatDuration(recordLong, context)}"
            }
        } else {
            iconImageView.setImageResource(R.drawable.knight_sad)
            titleTextView.text = context.getString(R.string.dommage)
            differenceTextView.setTextColor(ContextCompat.getColor(context, R.color.adapter_reder))
            if (lastRecord > 0) {
                recordTextView.text = context.getString(R.string.your_record, formatDuration(lastRecord, context))
                differenceTextView.text = "+ ${formatDuration(recordLong - lastRecord, context)}"
            }
        }
        builder.setView(dialogView)
        builder.setCancelable(false)
        builder.setPositiveButton(context.getString(R.string.close)) { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog = builder.create()
    }

    fun show() {
        alertDialog.show()
    }

    private fun formatDuration(milliseconds: Long, ctxt: Context): String {
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
        val seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000).toInt()
        val millis = (milliseconds % 1000).toInt()

        if (milliseconds == 0L) {
            return ctxt.getString(R.string.seconds, 0)
        }
        val sb = StringBuilder()
        if (hours > 0) {
            sb.append(ctxt.getString(R.string.hours, hours))
        }
        if (minutes > 0) {
            sb.append(ctxt.getString(R.string.minutes, minutes))
        }
        if (seconds > 0) {
            sb.append(ctxt.getString(R.string.seconds, seconds))
        }
        if (millis > 0) {
            sb.append(ctxt.getString(R.string.milliseconds, millis))
        }

        return sb.toString().trim()
    }
}