package com.diva.myapplication.kaprodi

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.diva.myapplication.databinding.FragmentEditJadwalBinding
import com.diva.myapplication.databinding.FragmentTambahJadwalBinding
import com.diva.myapplication.model.Jadwal
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class EditJadwalFragment : Fragment() {
    private var _binding: FragmentEditJadwalBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private var jadwal: Jadwal? = null

    companion object {
        private const val ARG_JADWAL = "jadwal"

        fun newInstance(jadwal: Jadwal): EditJadwalFragment {
            val fragment = EditJadwalFragment()
            val bundle = Bundle()
            bundle.putSerializable(ARG_JADWAL, jadwal) // Pastikan Jadwal implements Serializable
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance("https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
        jadwal = arguments?.getSerializable(ARG_JADWAL) as? Jadwal
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditJadwalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tampilkan data awal
        binding.edtHari.setText(jadwal?.hari)
        binding.edtJam.setText(jadwal?.jam)
        binding.edtRuangan.setText(jadwal?.ruangan)

        binding.btnUpdateJadwal.setOnClickListener {
            val hari = binding.edtHari.text.toString()
            val jam = binding.edtJam.text.toString()
            val ruangan = binding.edtRuangan.text.toString()

            val updatedJadwal = jadwal?.copy(hari = hari, jam = jam, ruangan = ruangan)

            if (updatedJadwal != null && jadwal?.key != null) {
                database.child("jadwal").child(jadwal!!.key!!).setValue(updatedJadwal)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Jadwal berhasil diupdate", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Gagal update jadwal", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
