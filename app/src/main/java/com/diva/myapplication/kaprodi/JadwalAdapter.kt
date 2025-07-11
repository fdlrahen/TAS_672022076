package com.diva.myapplication.kaprodi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.diva.myapplication.databinding.ItemJadwalBinding
import com.diva.myapplication.model.Jadwal

class JadwalAdapter(
    private val list: MutableList<Jadwal>,
    private val isKaprodi: Boolean = true,
    private val onEdit: (Jadwal, Int) -> Unit = { _, _ -> },
    private val onDelete: (Int) -> Unit = {}
) : RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemJadwalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Jadwal, position: Int) {
            binding.txtMatkul.text = "${item.kodeMatkul} - ${item.namaMatkul}"
            binding.txtDosen.text = "Dosen: ${item.namaDosen}"
            binding.txtHariJam.text = "${item.hari}, ${item.jam}"
            binding.txtRuangan.text = "Ruangan: ${item.ruangan}"
            binding.txtMahasiswa.text = "Mahasiswa: ${item.mahasiswa.joinToString(", ")}"

            if (isKaprodi) {
                binding.btnEdit.visibility = View.VISIBLE
                binding.btnDelete.visibility = View.VISIBLE

                binding.btnEdit.setOnClickListener {
                    onEdit(item, position)
                }

                binding.btnDelete.setOnClickListener {
                    onDelete(position)
                }
            } else {
                binding.btnEdit.visibility = View.GONE
                binding.btnDelete.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemJadwalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}
