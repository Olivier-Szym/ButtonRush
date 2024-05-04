package com.zimoliv.buttonrush

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.MenuItemCompat

class CustomActionView : LinearLayout, MenuItemCompat.OnActionExpandListener {
    private lateinit var iconImageView: ImageView
    private lateinit var titleTextView: TextView

    constructor(context: Context) : super(context) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initializeViews(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeViews(context)
    }

    private fun initializeViews(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.menu_item_custom, this, true)
        iconImageView = findViewById(R.id.icon)
        titleTextView = findViewById(R.id.title)
    }

    fun setTitle(title: String) {
        titleTextView.text = title
    }

    override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
        return true
    }

    override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
        return true
    }
}
