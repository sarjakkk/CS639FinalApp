package com.safe.resident.pro.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.safe.resident.pro.app.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@SignupActivity, R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()


        setupBackPressHandler()
        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            Log.e("SignUp", "onCreate: SignUp \n Email : $email \n Password: $password \n Re_Pass: $confirmPassword", )
            if (validateInputs(email, password, confirmPassword)) {
                signUpUser(email, password)
            }
        }

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }

    }

    private fun setupBackPressHandler() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle back press here, such as navigating to a different activity or fragment
                startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun validateInputs(email: String, password: String, confirmPassword: String): Boolean {
        if (email.isEmpty()) {
            showToast("Please enter your email.")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Invalid email format.")
            return false
        }

        if (password.isEmpty()) {
            showToast("Please enter a password.")
            return false
        }

        if (password.length < 8) {
            showToast("Password must be at least 8 characters long.")
            return false
        }

        if (password != confirmPassword) {
            showToast("Passwords do not match.")
            return false
        }

        return true
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Sign-up successful!")
                    startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
                    finish()
                } else {
                    showToast("Sign-up failed. Please try again.")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}