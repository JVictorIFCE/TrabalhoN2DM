package com.example.trabalhofinaln2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trabalhofinaln2.ui.theme.TrabalhoFinalN2Theme
import com.example.trabalhofinaln2.ui.theme.TrabalhoFinalN2Theme

class LoginActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val loginViewModel by viewModels<LoginViewModel> ()

        loginViewModel.setObserver { sucesso ->
            if (sucesso == true){
                startMainActivity()
            }else{
                Log.i("###", "Erro!")
            }
        }

        enableEdgeToEdge()
        setContent {
            TrabalhoFinalN2Theme{
                AppLogin(
                    showProgressBar = loginViewModel.logando,
                    onClickLogin = { email, senha ->
                        loginViewModel.logar(email, senha)
                    },
                    onCreateLogin = { email, senha ->
                        loginViewModel.cadastrar(email, senha)
                    },
                )
            }
        }
        super.onCreate(savedInstanceState)
    }

    fun startMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview(){
    TrabalhoFinalN2Theme {
        AppLogin()
    }
}

//Função de alta ordem

@Composable
fun AppLogin(
    showProgressBar: Boolean = false,
    onClickLogin:(String, String) -> Unit = {_,_->},
    onCreateLogin:(String, String) -> Unit = {_,_->}
){

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box (
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {Text("email")},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

                )
                OutlinedTextField(
                    value = senha,
                    onValueChange = { senha = it },
                    placeholder = {Text("senha")},
                    //visualTransformation = PasswordVisualTransformation()
                )
                TextButton(
                    onClick = {
                        onCreateLogin(email, senha)
                    }
                ) {Text("Cadastrar") }
                Button(
                    onClick = {
                        onClickLogin(email, senha)
                    }
                ) { Text("Logar") }
            }
            //marlos.maur@gmail.com
            if (showProgressBar){
                Box(modifier = Modifier
                    .fillMaxSize().background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}





