package com.example.trabalhofinaln2

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel: ViewModel() {

    val auth = FirebaseAuth.getInstance()
    var logando by mutableStateOf(false)

    var loginObserver: (Boolean) -> Unit = {}

    fun setObserver(localObserver: (Boolean) -> Unit){
        loginObserver = localObserver
    }

    fun logar(email: String, senha: String){
        logando = true
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                    loginObserver(true)
                }else {
                    loginObserver(false)
                    Log.i("###", task.exception?.message.toString())
                }
                logando = false
            }
    }

    fun cadastrar(email: String, senha: String){
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
            }
    }
