package com.diva.myapplication.dosen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.diva.myapplication.R
import com.diva.myapplication.databinding.FragmentKelasBinding
import com.diva.myapplication.model.Jadwal
import com.google.firebase.database.*

class KelasFragment : Fragment() {

    // --- Interface untuk Komunikasi dengan Activity ---
    interface OnFragmentInteractionListener {
        fun onNavigateToTambahNilai(jadwal: Jadwal)
        fun onNavigateToAbsensi(jadwal: Jadwal)
    }

    private var listener: OnFragmentInteractionListener? = null

    private var _binding: FragmentKelasBinding? = null
    private val binding get() = _binding!!

    private lateinit var database: DatabaseReference
    private lateinit var adapter: KelasAdapter
    private val daftarKelas = mutableListOf<Jadwal>()

    private var namaDosenLogin: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            namaDosenLogin = it.getString("NAMA_DOSEN")
            Log.d("KelasFragment", "Nama Dosen yang diterima: $namaDosenLogin")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKelasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = FirebaseDatabase.getInstance("https://myapplication-c53b1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        // Inisialisasi adapter dengan callback yang memanggil listener
        adapter = KelasAdapter(
            daftarKelas,
            onTambahNilaiClick = { jadwal ->
                listener?.onNavigateToTambahNilai(jadwal)
            },
            onAbsensiClick = { jadwal ->
                listener?.onNavigateToAbsensi(jadwal)
            }
        )

        binding.recyclerKelas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerKelas.adapter = adapter

        if (namaDosenLogin != null) {
            getKelasData(namaDosenLogin!!)
        }
    }

    private fun getKelasData(namaDosen: String) {
        database.child("jadwal").orderByChild("namaDosen").equalTo(namaDosen).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                daftarKelas.clear()
                for (data in snapshot.children) {
                    val jadwal = data.getValue(Jadwal::class.java)
                    if (jadwal != null) {
                        daftarKelas.add(jadwal)
                    }
                }
                adapter.notifyDataSetChanged()
                if (daftarKelas.isEmpty()) {
                    binding.tvNoData.visibility = View.VISIBLE
                    binding.recyclerKelas.visibility = View.GONE
                } else {
                    binding.tvNoData.visibility = View.GONE
                    binding.recyclerKelas.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}