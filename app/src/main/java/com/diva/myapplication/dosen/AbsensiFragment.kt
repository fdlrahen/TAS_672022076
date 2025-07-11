package com.diva.myapplication.dosen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diva.myapplication.databinding.FragmentAbsensiBinding
import com.diva.myapplication.model.Jadwal
import com.diva.myapplication.model.Mahasiswa
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AbsensiFragment : Fragment() {

    private var _binding: FragmentAbsensiBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference


    private var jadwal: Jadwal? = null
    private lateinit var adapter: MahasiswaAdapter
    private val daftarMahasiswa = mutableListOf<Mahasiswa>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            jadwal = it.getSerializable("KELAS_DATA") as? Jadwal
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAbsensiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ✅ Inisialisasi database
        database = FirebaseDatabase.getInstance("https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        adapter = MahasiswaAdapter(daftarMahasiswa)

        binding.recyclerMahasiswa.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMahasiswa.adapter = adapter

        binding.tvJudulAbsensi.text = "Absensi Kelas: ${jadwal?.namaMatkul ?: "-"}"

        jadwal?.let {
            tampilkanMahasiswa(it)
        }
    }


    /**
     * Tampilkan mahasiswa dari jadwal.mahasiswa (langsung dari list)
     */
    private fun tampilkanMahasiswa(jadwal: Jadwal) {
        daftarMahasiswa.clear()

        for (nama in jadwal.mahasiswa) {
            // Default kosong dulu
            val mahasiswa = Mahasiswa(
                nim = "",
                username = nama,
                nilai = "-"
            )
            daftarMahasiswa.add(mahasiswa)

            // Ambil nilai dari Firebase
            ambilNilaiMahasiswa(mahasiswa, jadwal.kodeMatkul)
        }

        adapter.notifyDataSetChanged()

        if (daftarMahasiswa.isEmpty()) {
            binding.tvNoData.visibility = View.VISIBLE
            binding.recyclerMahasiswa.visibility = View.GONE
        } else {
            binding.tvNoData.visibility = View.GONE
            binding.recyclerMahasiswa.visibility = View.VISIBLE
        }
    }

    /**
     * Ambil data nilai dari Firebase
     */
    private fun ambilNilaiMahasiswa(mahasiswa: Mahasiswa, kodeMatkul: String?) {
        if (kodeMatkul == null) return

        val namaKey = mahasiswa.username.replace(" ", "_")

        database.child("nilai").child(namaKey).child(kodeMatkul)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val nilaiData = snapshot.getValue(com.diva.myapplication.model.Nilai::class.java)
                    mahasiswa.nilai = nilaiData?.nilai ?: "-"
                    adapter.notifyDataSetChanged()  // Update tampilan
                }

                override fun onCancelled(error: DatabaseError) {
                    // Error, biarkan nilai tetap "-"
                }
            })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
