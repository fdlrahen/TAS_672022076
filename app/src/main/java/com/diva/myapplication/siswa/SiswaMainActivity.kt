package com.diva.myapplication.siswa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.diva.myapplication.R
import com.diva.myapplication.databinding.ActivitySiswaMainBinding

class SiswaMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySiswaMainBinding
    private var namaSiswaLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySiswaMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        namaSiswaLogin = intent.getStringExtra("NAMA_SISWA")

        // Tampilkan SiswaHomeFragment di awal
        if (savedInstanceState == null) {
            loadFragment(createSiswaHomeFragment())
        }

        binding.bottomNavSiswa.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_siswa_home -> {
                    loadFragment(createSiswaHomeFragment())
                    true
                }
                R.id.menu_hasil_studi -> {
                    loadFragment(createHasilStudiFragment())
                    true
                }
                R.id.menu_siswa_setting -> {
                    loadFragment(SiswaSettingFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun createSiswaHomeFragment(): SiswaHomeFragment {
        val fragment = SiswaHomeFragment()
        val bundle = Bundle()
        bundle.putString("NAMA_SISWA", namaSiswaLogin)
        fragment.arguments = bundle
        return fragment
    }
    private fun createHasilStudiFragment(): HasilStudiFragment {
        val fragment = HasilStudiFragment()
        val bundle = Bundle()
        bundle.putString("NAMA_SISWA", namaSiswaLogin)
        fragment.arguments = bundle
        return fragment
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.SiswaFragmentContainer, fragment)
            .commit()
    }
}
