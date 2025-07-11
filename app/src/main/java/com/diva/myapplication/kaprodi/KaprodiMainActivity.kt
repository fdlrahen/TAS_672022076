package com.diva.myapplication.kaprodi

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.diva.fragmentsiasat.kaprodi.KaprodiSettingFragment
import com.diva.myapplication.R
import com.diva.myapplication.databinding.ActivityKaprodiMainBinding

class KaprodiMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKaprodiMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKaprodiMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.kaprodiFragmentContainer, KaprodiHomeFragment())
            .commit()

        binding.bottomNavKaprodi.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_kaprodi_home -> {
                    loadFragment(KaprodiHomeFragment())
                    true
                }
                R.id.menu_tambah_jadwal -> {
                    loadFragment(TambahJadwalFragment())
                    true
                }
                R.id.menu_kaprodi_setting -> {
                    loadFragment(KaprodiSettingFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.kaprodiFragmentContainer, fragment)
            .commit()
    }
}