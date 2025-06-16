package com.ponto.controledeponto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ponto.controledeponto.databinding.ActivitySchedulerBinding

class schedulerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySchedulerBinding
    private val db = FirebaseFirestore.getInstance()

    private val servicosList = mutableListOf<Pair<String, String>>()
    private lateinit var adapter: ClinicaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchedulerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ClinicaAdapter(servicosList) { servico ->
            val intent = Intent(this, DetalheActivity::class.java).apply {
                putExtra("nome", servico.first)
                putExtra("valor", servico.second)
            }
            startActivity(intent)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        carregarServicos()

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun carregarServicos() {
        db.collection("servicos")
            .get()
            .addOnSuccessListener { result ->
                servicosList.clear() // Limpa a lista antes de adicionar novos
                for (document in result) {
                    val nome = document.getString("nome") ?: "Sem nome"
                    val valor = document.getString("valor") ?: "Sem valor"
                    servicosList.add(Pair(nome, "Valor: $valor"))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erro ao buscar serviços", Toast.LENGTH_SHORT).show()
                Log.e("Firestore", "Erro: ", exception)
            }
    }
}
