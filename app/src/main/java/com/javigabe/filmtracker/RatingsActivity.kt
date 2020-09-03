package com.javigabe.filmtracker

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RatingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ratings)
        getFilmRates()
    }

    fun getFilmRates() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val allEntries: Map<String, *> = prefs.getAll()

        for ((key, value) in allEntries) {
            val id = key.toIntOrNull()

            id?.let {
                val rating = value
            }
        }

    }
}