package com.diva.myapplication.siswa

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diva.myapplication.databinding.FragmentHasilStudiBinding
import com.diva.myapplication.model.Nilai
import com.google.firebase.database.*

class HasilStudiFragment : Fragment() {

    private var _binding: FragmentHasilStudiBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    private lateinit var nilaiAdapter: NilaiAdapter
    private val daftarNilai = mutableListOf<Nilai>()

    private var namaSiswaLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            namaSiswaLogin = it.getString("NAMA_SISWA")
        }

        database = FirebaseDatabase.getInstance("https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHasilStudiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nilaiAdapter = NilaiAdapter(daftarNilai)
        binding.recyclerHasilStudi.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHasilStudi.adapter = nilaiAdapter

        namaSiswaLogin?.let {
            tampilkanNilaiMahasiswa(it)
        } ?: Toast.makeText(requireContext(), "Nama siswa tidak ditemukan", Toast.LENGTH_SHORT).show()
    }

    private fun tampilkanNilaiMahasiswa(namaSiswa: String) {
        // Gunakan namaMahasiswa lengkap yang sesuai di database: "12345 - Mahasiswa A"
        val namaKey = namaSiswa.replace(" ", "_")

        database.child("nilai").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                daftarNilai.clear()

                for (data in snapshot.children) {
                    if (data.key?.contains(namaKey) == true) {
                        for (nilaiSnapshot in data.children) {
                            val nilai = nilaiSnapshot.getValue(Nilai::class.java)
                            if (nilai != null) {
                                daftarNilai.add(nilai)
                            }
                        }
                        break // Dapatkan hanya satu siswa
                    }
                }

                nilaiAdapter.notifyDataSetChanged()

                if (daftarNilai.isEmpty()) {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.recyclerHasilStudi.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.GONE
                    binding.recyclerHasilStudi.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data nilai", Toast.LENGTH_SHORT).show()
                Log.e("HasilStudiFragment", "Database error: ${error.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
