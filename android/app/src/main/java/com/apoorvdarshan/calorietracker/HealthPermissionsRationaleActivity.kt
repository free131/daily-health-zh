package com.apoorvdarshan.calorietracker

import android.app.Activity
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView

/** Health Connect privacy entry point, rendered entirely on device. */
class HealthPermissionsRationaleActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.private_privacy_title)
        val content = TextView(this).apply {
            text = getString(R.string.private_privacy_body)
            textSize = 18f
            val padding = (24 * resources.displayMetrics.density).toInt()
            setPadding(padding, padding, padding, padding)
        }
        setContentView(ScrollView(this).apply { addView(content) })
    }
}
