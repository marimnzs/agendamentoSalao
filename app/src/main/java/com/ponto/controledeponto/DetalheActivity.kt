package com.ponto.controledeponto

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ponto.controledeponto.databinding.ActivityDetalheBinding
import java.util.Calendar

class DetalheActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalheBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dados recebidos da intent
        val nome = intent.getStringExtra("nome")
        val valor = intent.getStringExtra("distancia")

        binding.tvNomeDetalhe.text = nome
        binding.tvDistanciaDetalhe.text = valor
        binding.tvDescricao.text =
            "Para caso de cancelamento nas 24 horas antecedentes ao serviço, é cobrada uma taxa de 50% do valor do serviço."

        // Dados de datas
        val meses = listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        val calendar = Calendar.getInstance()
        val mesAtual = calendar.get(Calendar.MONTH) // 0 = Janeiro
        val anoAtual = calendar.get(Calendar.YEAR)
        val diaAtual = calendar.get(Calendar.DAY_OF_MONTH)

        // Populando horários
        val horarios = mutableListOf<String>()
        for (hora in 8..18) {
            horarios.add(String.format("%02d:00", hora))
        }
        val adapterHorarios = ArrayAdapter(this, android.R.layout.simple_spinner_item, horarios)
        adapterHorarios.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHorario.adapter = adapterHorarios

        // Meses (apenas os próximos 3 meses)
        val fim = (mesAtual + 3).coerceAtMost(meses.size)
        val mesesFuturos = meses.subList(mesAtual, fim)
        val adapterMeses = ArrayAdapter(this, android.R.layout.simple_spinner_item, mesesFuturos)
        adapterMeses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMeses.adapter = adapterMeses

        // Função para pegar a quantidade de dias no mês
        fun getDiasNoMes(mes: Int, ano: Int): Int {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, ano)
            cal.set(Calendar.MONTH, mes)
            return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

        // Atualizar dias no spinner conforme mês selecionado
        fun atualizarDias(mesSelecionado: Int, anoSelecionado: Int) {
            val qtdDias = getDiasNoMes(mesSelecionado, anoSelecionado)
            val dias = mutableListOf<String>()

            val diaInicial = if (mesSelecionado == mesAtual) diaAtual + 1 else 1

            for (dia in diaInicial..qtdDias) {
                dias.add(dia.toString())
            }

            val adapterDias = ArrayAdapter(this, android.R.layout.simple_spinner_item, dias)
            adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerDiasMes.adapter = adapterDias
        }

        // Inicializa os dias com o mês atual
        atualizarDias(mesAtual, anoAtual)

        // Listener para quando mudar o mês
        binding.spinnerMeses.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?, position: Int, id: Long
            ) {
                val mesSelecionado = mesAtual + position
                atualizarDias(mesSelecionado, anoAtual)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Clique no botão de agendar
        binding.btnAgendar.setOnClickListener {
            val nomeServico = nome ?: "Desconhecido"
            val valorServico = valor ?: "Desconhecido"
            val mesSelecionado = binding.spinnerMeses.selectedItem?.toString() ?: ""
            val diaSelecionado = binding.spinnerDiasMes.selectedItem?.toString() ?: ""
            val horarioSelecionado = binding.spinnerHorario.selectedItem?.toString() ?: ""
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId == null) {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mesSelecionado.isBlank() || diaSelecionado.isBlank() || horarioSelecionado.isBlank()) {
                Toast.makeText(
                    this,
                    "Por favor, selecione mês, dia e horário",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val agendamento = hashMapOf(
                "servico" to nomeServico,
                "valor" to valorServico,
                "mes" to mesSelecionado,
                "dia" to diaSelecionado,
                "horario" to horarioSelecionado,
                "userId" to userId
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("agendamentos")
                .add(agendamento)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Agendamento realizado com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnAgendar.postDelayed({
                        val navegarSchedulerActivity =
                            Intent(this, schedulerActivity::class.java)
                        startActivity(navegarSchedulerActivity)
                        finish()
                    }, 500)
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Erro ao salvar agendamento",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
