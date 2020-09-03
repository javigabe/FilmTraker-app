package com.javigabe.filmtracker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.javigabe.filmtracker.resources.Film
import com.javigabe.filmtracker.resources.ImdbController
import kotlinx.android.synthetic.main.activity_film.*

class FilmActivity : AppCompatActivity() {
    private lateinit var myFilm: Film
    private lateinit var imdbController: ImdbController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getStringExtra("id")
        imdbController = ImdbController(this)
        imdbController.execute(id, "id")
    }

    fun setView(film: Film) {
        setContentView(R.layout.activity_film)
        myFilm = film
        filmNameFilm.text = film.name
        genreFilm.text = film.genre
        sinopsisFilm.text = film.sinopsis
        posterFilm.setImageBitmap(film.poster)

        checkIfFilmRated()
    }

    private fun checkIfFilmRated() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val rating = prefs.getString(myFilm.id, null)

        rating?.let {
            ratingGrade.setText(rating)
        }

        rateFilm()
    }


    private fun rateFilm() {
        rateButton.setOnClickListener {
            val grade = ratingGrade.text.toString().toIntOrNull()

            grade?.let {
                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.putString(myFilm.id, grade.toString())
                prefs.apply()

                Toast.makeText(this,  "Film rated !", Toast.LENGTH_LONG).show()
            } ?: run{
                Toast.makeText(this,  "Formato erroneo !", Toast.LENGTH_LONG).show()
            }
        }
    }
}