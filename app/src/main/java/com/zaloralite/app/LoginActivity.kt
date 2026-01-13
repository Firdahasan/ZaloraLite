package com.zaloralite.app

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zaloralite.app.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener {
            loginUser()
        }

        binding.tvToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validasi input
        if (email.isEmpty()) {
            binding.etEmail.error = "Email wajib diisi"
            return
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password wajib diisi"
            return
        }

        // Tampilkan loading, sembunyikan tombol
        showLoading(true)

        // Firebase Auth Login
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    // Login berhasil, pindah ke Home/MainActivity
                    Toast.makeText(this, "Selamat datang kembali!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Login Gagal: ${task.exception?.message}",
                        Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.btnLogin.visibility = View.VISIBLE
        }
    }
}