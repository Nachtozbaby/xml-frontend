package com.example.grocerylogin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvUsernameError: TextView
    private lateinit var tvPasswordError: TextView
    private lateinit var tvServerError: TextView
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        tvUsernameError = findViewById(R.id.tvUsernameError)
        tvPasswordError = findViewById(R.id.tvPasswordError)
        tvServerError = findViewById(R.id.tvServerError)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener { submitLogin() }
    }

    private fun submitLogin() {
        clearErrors()

        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString()

        var valid = true

        if (username.isEmpty()) {
            tvUsernameError.text = "Username is required."
            valid = false
        } else if (username.length < 3) {
            tvUsernameError.text = "Username must be at least 3 characters."
            valid = false
        }

        if (password.isEmpty()) {
            tvPasswordError.text = "Password is required."
            valid = false
        } else if (password.length < 8) {
            tvPasswordError.text = "Password must be at least 8 characters."
            valid = false
        }

        if (!valid) return

        // Frontend-only placeholder. Wire API/auth later.
        tvServerError.visibility = android.view.View.VISIBLE
        tvServerError.text = "Login UI validated. Connect API to continue."
    }

    private fun clearErrors() {
        tvUsernameError.text = ""
        tvPasswordError.text = ""
        tvServerError.text = ""
        tvServerError.visibility = android.view.View.GONE
    }
}
