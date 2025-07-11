package com.diva.myapplication.dosen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diva.myapplication.R
import com.diva.myapplication.databinding.FragmentDosenHomeBinding
import com.diva.myapplication.kaprodi.JadwalAdapter
import com.diva.myapplication.model.Jadwal
import com.google.firebase.database.*

class DosenHomeFragment : Fragment() {

    private var _binding: FragmentDosenHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var adapter: JadwalAdapter
    private val daftarJadwalDosen = mutableListOf<Jadwal>()

    private var namaDosenLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi Firebase Database
        val firebaseApp = FirebaseDatabase.getInstance("https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        database = firebaseApp.reference

        // Ambil nama dosen dari arguments
        arguments?.let {
            namaDosenLogin = it.getString("NAMA_DOSEN")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDosenHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val namaDosen = arguments?.getString("NAMA_DOSEN")

        binding.textViewNamaDosen.text = "Halo, $namaDosen"

        // Inisialisasi adapter
        adapter = JadwalAdapter(
            daftarJadwalDosen,
            isKaprodi = false
        )

        binding.recyclerJadwalDosen.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerJadwalDosen.adapter = adapter

        // Panggil fungsi untuk mengambil data jadwal setelah nama dosen tersedia
        if (namaDosenLogin != null) {
            getJadwalDosen(namaDosenLogin!!)
        } else {
            Toast.makeText(requireContext(), "Gagal memuat nama dosen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getJadwalDosen(namaDosen: String) {
        database.child("jadwal").orderByChild("namaDosen").equalTo(namaDosen).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("DosenHomeFragment", "Total jadwal untuk $namaDosen: ${snapshot.childrenCount}")
                daftarJadwalDosen.clear()
                for (data in snapshot.children) {
                    val jadwal = data.getValue(Jadwal::class.java)
                    if (jadwal != null) {
                        daftarJadwalDosen.add(jadwal)
                    }
                }
                adapter.notifyDataSetChanged()
                if (daftarJadwalDosen.isEmpty()) {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.recyclerJadwalDosen.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.GONE
                    binding.recyclerJadwalDosen.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DosenHomeFragment", "Gagal memuat jadwal: ${error.message}")
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}