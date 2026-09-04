package com.absensiseleksi.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View

object ViewUtils {
    @SuppressLint("ClickableViewAccessibility")
    fun View.addClickAnimation() {
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                }
            }
            false
        }
    }
}