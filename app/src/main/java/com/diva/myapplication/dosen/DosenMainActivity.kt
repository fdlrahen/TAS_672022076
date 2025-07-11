package com.diva.myapplication.dosen

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.diva.myapplication.R
import com.diva.myapplication.databinding.ActivityDosenMainBinding
import com.diva.myapplication.model.Jadwal

// Tambahkan implementasi interface di sini
class DosenMainActivity : AppCompatActivity(), KelasFragment.OnFragmentInteractionListener {
    private lateinit var binding: ActivityDosenMainBinding
    private var namaDosenLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDosenMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        namaDosenLogin = intent.getStringExtra("NAMA_DOSEN")

        if (savedInstanceState == null) {
            loadFragment(createDosenHomeFragment())
        }

        binding.bottomNavDosen.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_dosen_home -> {
                    loadFragment(createDosenHomeFragment())
                    true
                }
                R.id.menu_mata_kuliah -> {
                    loadFragment(createKelasFragment())
                    true
                }
                R.id.menu_dosen_setting -> {
                    loadFragment(DosenSettingFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Metode dari interface KelasFragment.OnFragmentInteractionListener
    override fun onNavigateToTambahNilai(jadwal: Jadwal) {
        val fragment = TambahNilaiFragment()
        val bundle = Bundle().apply {
            putSerializable("KELAS_DATA", jadwal)
        }
        fragment.arguments = bundle
        loadFragment(fragment)
    }

    // Metode dari interface KelasFragment.OnFragmentInteractionListener
    override fun onNavigateToAbsensi(jadwal: Jadwal) {
        val fragment = AbsensiFragment()
        val bundle = Bundle().apply {
            putSerializable("KELAS_DATA", jadwal)
        }
        fragment.arguments = bundle
        loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.DosenFragmentContainer, fragment)
            .commit()
    }

    private fun createDosenHomeFragment(): DosenHomeFragment {
        val fragment = DosenHomeFragment()
        val bundle = Bundle()
        bundle.putString("NAMA_DOSEN", namaDosenLogin)
        fragment.arguments = bundle
        return fragment
    }

    private fun createKelasFragment(): KelasFragment {
        val fragment = KelasFragment()
        val bundle = Bundle()
        bundle.putString("NAMA_DOSEN", namaDosenLogin)
        fragment.arguments = bundle
        return fragment
    }
}