package com.diva.myapplication.kaprodi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.diva.myapplication.R
import com.diva.myapplication.databinding.FragmentKaprodiHomeBinding
import com.diva.myapplication.model.Jadwal
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class KaprodiHomeFragment : Fragment() {
    private var _binding: FragmentKaprodiHomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var database: DatabaseReference
    private lateinit var adapter: JadwalAdapter
    // Gunakan daftarJadwal sebagai variabel yang bisa diakses adapter
    private val daftarJadwal = mutableListOf<Jadwal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseApp = FirebaseDatabase.getInstance("https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        database = firebaseApp.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentKaprodiHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi adapter sebelum mengambil data, ini sudah benar
        adapter = JadwalAdapter(
            daftarJadwal,
            isKaprodi = true,
            onEdit = { jadwal, _ ->
                Log.d("KaprodiHomeFragment", "Edit diklik untuk: ${jadwal.namaMatkul}")
                val editFragment = EditJadwalFragment.newInstance(jadwal)
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.kaprodiFragmentContainer, editFragment)
                    .addToBackStack(null)
                    .commit()
            },

            onDelete = { pos ->
                val itemToDelete = daftarJadwal[pos]
                val key = itemToDelete.key  // ambil key asli dari Firebase

                if (key != null) {
                    database.child("jadwal").child(key).removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Jadwal dihapus", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Gagal menghapus", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Key tidak ditemukan, gagal hapus", Toast.LENGTH_SHORT).show()
                }

            }

        )

        binding.recyclerMataKuliah.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerMataKuliah.adapter = adapter

        // Panggil fungsi pengambilan data setelah adapter disetel
        getJadwalData()


    }

    private fun getJadwalData() {
        database.child("jadwal").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Firebase", "Total Data: ${snapshot.childrenCount}")
                val newJadwalList = mutableListOf<Jadwal>()
                for (data in snapshot.children) {
                    val jadwal = data.getValue(Jadwal::class.java)
                    if (jadwal != null) {
                        jadwal?.key = data.key
                        newJadwalList.add(jadwal)
                    }
                }

                // Panggil metode untuk memperbarui daftar dan adapter
                updateJadwalList(newJadwalList)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error saat mengambil data: ${error.message}")
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Metode baru untuk memperbarui daftar dan adapter
    private fun updateJadwalList(newList: List<Jadwal>) {
        // Hapus data lama
        daftarJadwal.clear()
        // Tambahkan data baru
        daftarJadwal.addAll(newList)
        // Beri tahu adapter bahwa datanya telah berubah
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}