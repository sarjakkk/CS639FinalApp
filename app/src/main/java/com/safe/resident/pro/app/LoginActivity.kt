package com.safe.resident.pro.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.safe.resident.pro.app.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@LoginActivity, R.layout.activity_login)

        setupBackPressHandler()

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rootView = findViewById<View>(android.R.id.content)
            val message = "Enter Valid Credentials"
            val duration = Snackbar.LENGTH_SHORT
            Log.e("Login", "onCreate: Login \n Email : $email \n Password: $password")
            if (validateCredentials(email, password)) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    email,
                    password
                )
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = FirebaseAuth.getInstance().currentUser

                            val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                            sharedPrefs.edit().putBoolean("isLoggedIn", true).apply()

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }else{
                val snackbar = Snackbar.make(rootView, message, duration)
                snackbar.show()
            }
        }
    }
    private fun setupBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }
    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        // Example: Password must be at least 8 characters and include numbers and letters
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$"
        val passwordMatcher = Regex(passwordPattern)

        return password.isNotEmpty() && passwordMatcher.matches(password)
    }

    fun validateCredentials(email: String, password: String): Boolean {
        var valid = true

        if (!isValidEmail(email)) {
            // Handle error for email
            println("Invalid email format")
            valid = false
        }

        if (!isValidPassword(password)) {
            // Handle error for password
            println("Password must be at least 8 characters long and include both letters and numbers.")
            valid = false
        }

        return valid
    }
}