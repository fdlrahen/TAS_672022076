package com.diva.myapplication.siswa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diva.myapplication.databinding.ItemNilaiBinding
import com.diva.myapplication.model.Nilai

class NilaiAdapter(
    private val list: List<Nilai>
) : RecyclerView.Adapter<NilaiAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemNilaiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(nilai: Nilai) {
            binding.tvMatkul.text = "${nilai.kodeMatkul} - ${nilai.namaMatkul}"
            binding.tvNilai.text = "Nilai: ${nilai.nilai ?: "-"}"
            binding.tvDosen.text = "Dosen: ${nilai.namaDosen ?: "-"}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNilaiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
