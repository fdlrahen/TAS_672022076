package com.diva.myapplication.dosen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diva.myapplication.databinding.FragmentTambahNilaiBinding
import com.diva.myapplication.model.Jadwal
import com.diva.myapplication.model.Nilai
import com.google.firebase.database.*
import java.util.ArrayList
import java.util.Locale

class TambahNilaiFragment : Fragment() {

    private var _binding: FragmentTambahNilaiBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private var namaDosenLogin: String? = null
    private val daftarJadwalDosen = mutableListOf<Jadwal>()
    private val daftarMahasiswa = mutableSetOf<String>()
    private val daftarMataKuliah = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseApp = FirebaseDatabase.getInstance("https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/")
        database = firebaseApp.reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTambahNilaiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil nama dosen dari Activity
        namaDosenLogin = activity?.intent?.getStringExtra("NAMA_DOSEN")

        if (namaDosenLogin == null) {
            Toast.makeText(requireContext(), "Nama Dosen tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        // Ambil data jadwal dari Firebase untuk mengisi Spinner
        getJadwalDosen(namaDosenLogin!!)

        binding.btnSimpanNilai.setOnClickListener {
            simpanNilai()
        }
    }

    private fun getJadwalDosen(namaDosen: String) {
        database.child("jadwal").orderByChild("namaDosen").equalTo(namaDosen).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                daftarJadwalDosen.clear()
                daftarMahasiswa.clear()
                daftarMataKuliah.clear()

                for (data in snapshot.children) {
                    val jadwal = data.getValue(Jadwal::class.java)
                    if (jadwal != null) {
                        daftarJadwalDosen.add(jadwal)
                        daftarMahasiswa.addAll(jadwal.mahasiswa) // Tambahkan semua mahasiswa
                        daftarMataKuliah.add(jadwal.namaMatkul) // Tambahkan semua mata kuliah
                    }
                }

                // Isi Spinner setelah data didapatkan
                setupSpinners()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TambahNilaiFragment", "Gagal memuat jadwal: ${error.message}")
                Toast.makeText(requireContext(), "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinners() {
        val matkulAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ArrayList(daftarMataKuliah))
        matkulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMatkulNilai.adapter = matkulAdapter

        val mahasiswaAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ArrayList(daftarMahasiswa))
        mahasiswaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMahasiswa.adapter = mahasiswaAdapter
    }

    private fun simpanNilai() {
        val selectedMatkul = binding.spinnerMatkulNilai.selectedItem as String?
        val selectedMahasiswa = binding.spinnerMahasiswa.selectedItem as String?
        val nilaiString = binding.edtNilai.text.toString().trim()

        if (selectedMatkul.isNullOrEmpty() || selectedMahasiswa.isNullOrEmpty() || nilaiString.isEmpty()) {
            Toast.makeText(requireContext(), "Harap isi semua data", Toast.LENGTH_SHORT).show()
            return
        }

        val jadwalTerkait = daftarJadwalDosen.firstOrNull { it.namaMatkul == selectedMatkul }
        val kodeMatkul = jadwalTerkait?.kodeMatkul ?: "NO_KODE"

        val nilaiBaru = Nilai(
            namaDosen = namaDosenLogin,
            namaMahasiswa = selectedMahasiswa,
            namaMatkul = selectedMatkul,
            kodeMatkul = kodeMatkul,
            nilai = nilaiString.uppercase(Locale.getDefault())
        )

        // Simpan ke Firebase. Path: /nilai/{nama_mahasiswa}/{kode_matkul}
        val nilaiKey = selectedMahasiswa.replace(" ", "_")
        database.child("nilai").child(nilaiKey).child(kodeMatkul).setValue(nilaiBaru)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Nilai berhasil disimpan!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menyimpan nilai", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}