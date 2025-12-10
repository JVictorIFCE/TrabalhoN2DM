package com.example.trabalhofinaln2

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

    fun limparErro() {
        erro = null
    }

    fun logar(email: String, senha: String) {
        logando = true
        if (email.isBlank() || senha.isBlank()) {
            erro = "Preencha e-mail e senha!"
            logando = false
            return
        }

        if (senha.length < 6) {
            erro = "A senha deve ter pelo menos 6 caracteres!"
            logando = false
            return
        }
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginObserver(true)
                } else {
                    loginObserver(false)
                    erro = task.exception?.message
                        ?: "Erro ao realizar login"
                }
                logando = false
            }
    }

    fun cadastrar(email: String, senha: String) {

        sucesso = false
        erro = null

        if (email.isBlank() || senha.isBlank()) {
            erro = "Preencha e-mail e senha!"
            return
        }

        if (senha.length < 6) {
            erro = "A senha deve ter pelo menos 6 caracteres!"
            return
        }

        logando = true

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                logando = false
                if (task.isSuccessful) {
                    sucesso = true
                } else {
                    erro = task.exception?.message ?: "Erro ao cadastrar"
                }
            }
    }
}