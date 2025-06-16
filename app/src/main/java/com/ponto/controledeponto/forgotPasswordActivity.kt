package com.ponto.controledeponto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.ponto.controledeponto.databinding.ActivityForgotPasswordBinding
import com.ponto.controledeponto.databinding.ActivityLoginBinding

class forgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.btnForgotPassword.setOnClickListener {
            val email: String = binding.etEmail.text.toString()
            val navegarLogin = Intent(this, LoginActivity::class.java )
            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu e-mail cadastrado", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        Toast.makeText(baseContext, "Email enviado com sucesso", Toast.LENGTH_LONG).show()
                        startActivity(navegarLogin)
//                val user = auth.currentUser
                    } else {
                        Toast.makeText(baseContext, "Email inválido", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}