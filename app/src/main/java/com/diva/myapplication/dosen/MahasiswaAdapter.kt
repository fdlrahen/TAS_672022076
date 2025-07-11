package com.diva.myapplication.dosen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diva.myapplication.databinding.ItemMahasiswaBinding
import com.diva.myapplication.model.Mahasiswa

class MahasiswaAdapter(
    private val mahasiswaList: List<Mahasiswa>
) : RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder>() {

    inner class MahasiswaViewHolder(
        private val binding: ItemMahasiswaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(mahasiswa: Mahasiswa) {
            binding.tvNamaMahasiswa.text = mahasiswa.username
            binding.tvNim.text = "NIM: ${mahasiswa.nim}"
            binding.tvNilai.text = "Nilai: ${if (mahasiswa.nilai.isNotEmpty()) mahasiswa.nilai else "-"}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MahasiswaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMahasiswaBinding.inflate(inflater, parent, false)
        return MahasiswaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MahasiswaViewHolder, position: Int) {
        holder.bind(mahasiswaList[position])
    }

    override fun getItemCount(): Int = mahasiswaList.size
}
