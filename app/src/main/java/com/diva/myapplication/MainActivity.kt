package com.diva.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.diva.myapplication.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Tambahkan inisialisasi FirebaseApp di sini
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Inisialisasi database setelah FirebaseApp
        database = FirebaseDatabase.getInstance().reference

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnLogin.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val password = binding.edtPassword.text.toString().trim()

            when {
                username == "kaprodi" && password == "6998" -> {
                    loginSebagai(username, "kaprodi")
                }
                username == "Dosen A" && password == "1234" -> {
                    loginSebagai(username, "dosen")
                }
                username == "Dosen B" && password == "1235" -> {
                    loginSebagai(username, "dosen")
                }
                username == "Dosen C" && password == "1235" -> {
                    loginSebagai(username, "dosen")
                }
                username == "Mahasiswa A" && password == "5678" -> {
                    loginSebagai(username, "siswa")
                }
                username == "Mahasiswa B" && password == "5679" -> {
                    loginSebagai(username, "siswa")
                }
                username == "Mahasiswa C" && password == "5680" -> {
                    loginSebagai(username, "siswa")
                }
                username == "Mahasiswa D" && password == "5681" -> {
                    loginSebagai(username, "siswa")
                }
                else -> {
                    Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loginSebagai(username: String, role: String) {
        val pref = getSharedPreferences("user_session", MODE_PRIVATE)
        pref.edit().putString("role", role).apply()

        val userData = mapOf(
            "username" to username,
            "role" to role,
            "status" to "login"
        )

        // Simpan ke node "logins/username"
        database.child("logins").child(username).setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "Data login tersimpan", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal simpan data login", Toast.LENGTH_SHORT).show()
            }
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("USERNAME", username)
        startActivity(intent)
        finish()
    }

}