package com.javigabe.filmtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_film.*

class FilmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_film)
        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val genre = intent.getStringExtra("genre")
        //val poster = intent.getByteArrayExtra("poster")

        filmNameFilm.setText(name)
    }
}