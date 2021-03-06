package com.javigabe.filmtracker

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.javigabe.filmtracker.resources.Film
import com.javigabe.filmtracker.resources.ImdbController


class HomeActivity : AppCompatActivity() {

    private lateinit var filmManager: ImdbController
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: HomeAdapter
    private lateinit var mLayoutManager: RecyclerView.LayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        saveAuthenticatedUser()
        filmManager = ImdbController(this@HomeActivity)
    }

    override fun onStart() {
        super.onStart()
        setContentView(R.layout.activity_home)
    }


     fun setUpRecyclerView(films: ArrayList<Film>) {
        mRecyclerView = findViewById(R.id.homeRecyclerView)
        mRecyclerView.setHasFixedSize(true)

        mLayoutManager = LinearLayoutManager(this)
        mAdapter = HomeAdapter(films)
        mRecyclerView.setLayoutManager(mLayoutManager)
        mRecyclerView.setAdapter(mAdapter)

         mAdapter.setOnItemClickListener(object : HomeAdapter.OnItemClickListener {
             override fun onItemClick(position: Int) {
                 val film = films.get(position)
                 val intent = Intent(baseContext, FilmActivity::class.java)
                 intent.putExtra("id", film.id)
                 startActivity(intent)
             }
         })
    }


    private fun saveAuthenticatedUser() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        val user = FirebaseAuth.getInstance().currentUser

        user?.let {
            prefs.putString("email", user.email)
            prefs.apply()
        }
    }

    private fun showAuthLayout() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
    }

    override fun onBackPressed() {
        // Get out the app when pressing back in the home view
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        //StrictMode.setThreadPolicy(policy)

        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)

        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        val searchItem = menu?.findItem(R.id.searchButton)
        val searchView = searchItem?.actionView as SearchView

        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                searchItem.collapseActionView()

                // If multiple consecutive fast searches are done, only take last one
                filmManager.cancel(true)
                filmManager = ImdbController(this@HomeActivity)
                filmManager.execute(query, "title")

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.logOutButton -> {
                val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                FirebaseAuth.getInstance().signOut()
                prefs.clear().apply()
                showAuthLayout()
                true
            }
            R.id.profileButton -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.ratingsButton -> {
                val intent = Intent(this, RatingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}