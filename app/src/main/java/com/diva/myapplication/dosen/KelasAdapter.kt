package com.diva.myapplication.dosen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diva.myapplication.databinding.ItemKelasDosenBinding
import com.diva.myapplication.model.Jadwal

class KelasAdapter(
    private val list: List<Jadwal>,
    private val onTambahNilaiClick: (Jadwal) -> Unit,
    private val onAbsensiClick: (Jadwal) -> Unit
) : RecyclerView.Adapter<KelasAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemKelasDosenBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Jadwal) {
            // Mengisi data ke dalam TextViews
            binding.tvNamaKelas.text = "${item.kodeMatkul} - ${item.namaMatkul}"
            binding.tvDosen.text = "Dosen: ${item.namaDosen}"
            binding.tvHariJam.text = "${item.hari}, ${item.jam}"
            binding.tvRuangan.text = "Ruangan: ${item.ruangan}"

            // Menetapkan listener untuk tombol
            binding.btnTambahNilai.setOnClickListener {
                onTambahNilaiClick(item)
            }

            binding.btnAbsensi.setOnClickListener {
                onAbsensiClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemKelasDosenBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}