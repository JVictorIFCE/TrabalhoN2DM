package com.example.trabalhofinaln2

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    val auth = FirebaseAuth.getInstance()

    // Estados do login
    var logando by mutableStateOf(false)

    // Estados do cadastro
    var erro by mutableStateOf<String?>(null)
    var sucesso by mutableStateOf(false)

    var loginObserver: (Boolean) -> Unit = {}

    fun setObserver(localObserver: (Boolean) -> Unit) {
        loginObserver = localObserver
    }

    fun logar(email: String, senha: String) {
        logando = true
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginObserver(true)
                } else {
                    loginObserver(false)
                    Log.i("###", task.exception?.message.toString())
                }
                logando = false
            }
    }

    fun cadastrar(email: String, senha: String) {
        erro = null
        sucesso = false

        if (email.isBlank() || senha.isBlank()) {
            erro = "Preencha e-mail e senha!"
            return
        }

        if (senha.length < 6) {
            erro = "A senha deve ter pelo menos 6 caracteres!"
            return
        }

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sucesso = true
                } else {
                    erro = task.exception?.message ?: "Erro ao cadastrar"
                }
            }
    }
}
