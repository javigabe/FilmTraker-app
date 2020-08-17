package com.javigabe.filmtracker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = "AUTH_ACTIVITY"
    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        checkAlreadyAuthenticated()
    }

    private fun checkAlreadyAuthenticated() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val user = prefs.getString("email", null)
        user?.let{
            showHomeLayout()
        } ?: run {
            logInSetup()
        }
    }

    private fun logInSetup() {
        setContentView(R.layout.activity_auth)
        auth = FirebaseAuth.getInstance()

        registerButton.setOnClickListener {
            if (emailText.text.isNotEmpty() && passwordText.text.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHomeLayout()
                    } else {
                        Log.e(TAG, "No se ha podido registrar")
                        showAlert(getString(R.string.register_alert))
                    }
                }
            }
        }

        logInButton.setOnClickListener{
            if (emailText.text.isNotEmpty() && passwordText.text.isNotEmpty()) {
                auth.signInWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHomeLayout()
                            } else {
                                Log.e(TAG, getString(R.string.log_in_alert))
                                showAlert(getString(R.string.log_in_alert))
                            }
                        }
            }
        }

        googleSignInButton.setOnClickListener {
            val googleConfig = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail().build()
            val googleClient = GoogleSignIn.getClient(this, googleConfig)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }

    private fun showHomeLayout() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun showAlert(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignIn.signInIntent
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                account?.let {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHomeLayout()
                        } else {
                            showAlert("Ha habido un error al iniciar sesion con Google")
                        }
                    }
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e)
            }
        }
    }

    override fun onBackPressed() {
        // Get out the app when pressing back in the auth view
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}