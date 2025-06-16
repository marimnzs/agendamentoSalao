package com.ponto.controledeponto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ponto.controledeponto.databinding.ItemClinicaBinding

class ClinicaAdapter(
    private val clinicas: List<Pair<String, String>>,
    private val onClick: (Pair<String, String>) -> Unit
) : RecyclerView.Adapter<ClinicaAdapter.ClinicaViewHolder>() {

    inner class ClinicaViewHolder(val binding: ItemClinicaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clinica: Pair<String, String>) {
            binding.tvNome.text = clinica.first
            binding.tvDistancia.text = clinica.second
            binding.root.setOnClickListener { onClick(clinica) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClinicaViewHolder {
        val binding = ItemClinicaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClinicaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClinicaViewHolder, position: Int) {
        holder.bind(clinicas[position])
    }

    override fun getItemCount() = clinicas.size
}

