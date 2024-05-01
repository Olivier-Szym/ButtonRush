package com.zimoliv.buttonrush.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class ClickAnimationView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint: Paint = Paint()
    private val animations: MutableList<ClickAnimation> = mutableListOf()

    init {
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.alpha = 170
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (animation in animations) {
            canvas.drawCircle(animation.centerX, animation.centerY, animation.radius, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // L'utilisateur a appuyé sur l'écran, obtenir les coordonnées du clic
                val newAnimation = ClickAnimation(event.x, event.y, 0f)
                animations.add(newAnimation)
                // Déclencher l'animation
                startAnimation(newAnimation)
                return false
            }
        }
        return false
    }

    private fun startAnimation(animation: ClickAnimation) {
        // Utiliser un ValueAnimator ou un Thread pour animer l'expansion du cercle
        val animator = android.animation.ValueAnimator.ofFloat(0f, 50f, 0f)
        animator.duration = 400 // Durée de l'animation en millisecondes

        animator.addUpdateListener { valueAnimator ->
            animation.radius = valueAnimator.animatedValue as Float
            invalidate()
        }

        animator.start()
    }

    private data class ClickAnimation(val centerX: Float, val centerY: Float, var radius: Float)
}

