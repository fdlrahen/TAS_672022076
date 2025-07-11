package com.diva.myapplication.siswa

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diva.myapplication.MainActivity
import com.diva.myapplication.R
import com.diva.myapplication.databinding.FragmentSiswaSettingBinding
import com.google.firebase.auth.FirebaseAuth

class SiswaSettingFragment : Fragment() {

    private var _binding: FragmentSiswaSettingBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi Firebase Auth di onCreate
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menggunakan ViewBinding untuk meng-inflate layout
        _binding = FragmentSiswaSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Menangani klik tombol logout menggunakan ViewBinding
        binding.btnLogout.setOnClickListener {
            // Lakukan proses logout
            auth.signOut()

            // Arahkan pengguna kembali ke MainActivity (halaman login)
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Bersihkan stack aktivitas
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Mengatur binding menjadi null untuk menghindari memory leak
        _binding = null
    }
}