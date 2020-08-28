package com.javigabe.filmtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.javigabe.filmtracker.resources.ImdbController
import kotlinx.android.synthetic.main.activity_film.*

class FilmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film)
        val id = intent.getStringExtra("id")

        val imdbController = ImdbController(this)
        imdbController.execute(id, "id")

        filmNameFilm.setText(name)
    }
}