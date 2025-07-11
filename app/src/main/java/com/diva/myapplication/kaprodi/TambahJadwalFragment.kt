package com.diva.myapplication.kaprodi

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.diva.myapplication.databinding.FragmentTambahJadwalBinding
import com.diva.myapplication.model.Jadwal
import com.diva.myapplication.model.MataKuliah
import com.google.firebase.database.*
import java.util.*

class TambahJadwalFragment : Fragment() {

    private var _binding: FragmentTambahJadwalBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference

    private val daftarMatkul = mutableListOf<MataKuliah>()
    private val daftarDosen = mutableListOf<String>()
    private val daftarMahasiswa = mutableListOf<String>()
    private val selectedMahasiswaList = mutableListOf<String>()

    private val DB_URL = "https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance(DB_URL).reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTambahJadwalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ambilDataMataKuliah()
        ambilDataDosen()
        ambilDataMahasiswa()

        binding.edtJam.setOnClickListener { showTimePickerDialog() }
        binding.edtMahasiswa.setOnClickListener { showMahasiswaSelectionDialog() }

        binding.btnSimpanJadwal.setOnClickListener {
            simpanJadwal()
        }
    }

    private fun ambilDataMataKuliah() {
        database.child("matakuliah").get().addOnSuccessListener { snapshot ->
            daftarMatkul.clear()
            daftarMatkul.addAll(snapshot.children.mapNotNull { it.getValue(MataKuliah::class.java) })

            val matkulAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item,
                daftarMatkul.map { "${it.kode} - ${it.nama}" }
            )
            matkulAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerMatkul.adapter = matkulAdapter
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal memuat mata kuliah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ambilDataDosen() {
        database.child("dosen").get().addOnSuccessListener { snapshot ->
            daftarDosen.clear()
            daftarDosen.addAll(snapshot.children.mapNotNull { it.child("nama").getValue(String::class.java) })

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daftarDosen)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDosen.adapter = adapter
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal memuat dosen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun ambilDataMahasiswa() {
        database.child("mahasiswa").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                daftarMahasiswa.clear()
                for (data in snapshot.children) {
                    val nim = data.child("nim").getValue(String::class.java)
                    val nama = data.child("username").getValue(String::class.java)
                    if (nim != null && nama != null) {
                        daftarMahasiswa.add("$nim - $nama")
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memuat data mahasiswa", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val timeStartPicker = TimePickerDialog(requireContext(), { _, hourStart, minuteStart ->
            val jamMulai = String.format("%02d:%02d", hourStart, minuteStart)
            val timeEndPicker = TimePickerDialog(requireContext(), { _, hourEnd, minuteEnd ->
                val jamSelesai = String.format("%02d:%02d", hourEnd, minuteEnd)
                binding.edtJam.setText("$jamMulai - $jamSelesai")
            }, hourStart, minuteStart, true)
            timeEndPicker.show()
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timeStartPicker.show()
    }

    private fun showMahasiswaSelectionDialog() {
        val selectedItems = BooleanArray(daftarMahasiswa.size) { daftarMahasiswa[it] in selectedMahasiswaList }
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Mahasiswa")
            .setMultiChoiceItems(daftarMahasiswa.toTypedArray(), selectedItems) { _, which, isChecked ->
                val item = daftarMahasiswa[which]
                if (isChecked) {
                    if (!selectedMahasiswaList.contains(item)) {
                        selectedMahasiswaList.add(item)
                    }
                } else {
                    selectedMahasiswaList.remove(item)
                }
            }
            .setPositiveButton("OK") { _, _ ->
                binding.edtMahasiswa.setText(selectedMahasiswaList.joinToString(", "))
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun simpanJadwal() {
        val hari = binding.edtHari.text.toString().trim()
        val jam = binding.edtJam.text.toString().trim()
        val ruangan = binding.edtRuangan.text.toString().trim()
        val selectedMatkul = daftarMatkul.getOrNull(binding.spinnerMatkul.selectedItemPosition)
        val selectedDosen = daftarDosen.getOrNull(binding.spinnerDosen.selectedItemPosition)

        if (hari.isEmpty() || jam.isEmpty() || ruangan.isEmpty() || selectedMahasiswaList.isEmpty() || selectedMatkul == null || selectedDosen == null) {
            Toast.makeText(requireContext(), "Isi semua field dan pilih mahasiswa", Toast.LENGTH_SHORT).show()
            return
        }

        val jadwalBaru = Jadwal(
            kodeMatkul = selectedMatkul.kode,
            namaMatkul = selectedMatkul.nama,
            namaDosen = selectedDosen,
            hari = hari,
            jam = jam,
            ruangan = ruangan,
            mahasiswa = selectedMahasiswaList
        )

        val key = database.child("jadwal").push().key ?: return

        database.child("jadwal").child(key).setValue(jadwalBaru)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Jadwal berhasil ditambahkan!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menyimpan jadwal", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
