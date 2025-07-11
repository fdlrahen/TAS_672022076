package com.diva.myapplication.siswa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diva.myapplication.databinding.FragmentSiswaHomeBinding
import com.diva.myapplication.kaprodi.JadwalAdapter
import com.diva.myapplication.model.Jadwal
import com.google.firebase.database.*

class SiswaHomeFragment : Fragment() {

    private var _binding: FragmentSiswaHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var adapter: JadwalAdapter
    private val daftarJadwalSiswa = mutableListOf<Jadwal>()

    private var namaSiswaLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance("https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference

        // Ambil nama siswa dari arguments (pastikan key-nya sama)
        arguments?.let {
            namaSiswaLogin = it.getString("NAMA_SISWA")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSiswaHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewNamaSiswa.text = "Halo, $namaSiswaLogin"

        // Inisialisasi adapter
        adapter = JadwalAdapter(
            daftarJadwalSiswa,
            isKaprodi = false
        )

        binding.recyclerJadwalSiswa.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerJadwalSiswa.adapter = adapter

        if (!namaSiswaLogin.isNullOrEmpty()) {
            getJadwalSiswa(namaSiswaLogin!!)
        } else {
            Toast.makeText(requireContext(), "Nama siswa tidak tersedia", Toast.LENGTH_SHORT).show()
            Log.e("SiswaHomeFragment", "Nama siswa kosong/null")
        }
    }

    private fun getJadwalSiswa(namaSiswa: String) {
        database.child("jadwal").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                daftarJadwalSiswa.clear()
                for (data in snapshot.children) {
                    val jadwal = data.getValue(Jadwal::class.java)
                    if (jadwal != null && jadwal.mahasiswa.any { it.contains(namaSiswa, ignoreCase = true) }) {
                        daftarJadwalSiswa.add(jadwal)
                    }
                }

                adapter.notifyDataSetChanged()

                if (daftarJadwalSiswa.isEmpty()) {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.recyclerJadwalSiswa.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.GONE
                    binding.recyclerJadwalSiswa.visibility = View.VISIBLE
                }

                Log.d("SiswaHomeFragment", "Jumlah jadwal ditemukan: ${daftarJadwalSiswa.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
                Log.e("SiswaHomeFragment", "onCancelled: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
