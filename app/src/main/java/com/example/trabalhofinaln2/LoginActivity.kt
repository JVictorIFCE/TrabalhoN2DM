package com.example.trabalhofinaln2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhofinaln2.ui.theme.TrabalhoFinalN2Theme

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        val loginViewModel by viewModels<LoginViewModel>()

        loginViewModel.setObserver { sucesso ->
            if (sucesso) {
                startMainActivity()
            } else {
                Log.i("###", "Erro!")
            }
        }

        enableEdgeToEdge()
        setContent {
            TrabalhoFinalN2Theme {
                AppLogin(
                    erro = loginViewModel.erro,
                    onErroConsumido = {
                        loginViewModel.limparErro()
                    },
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

    fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    TrabalhoFinalN2Theme {
        AppLogin()
    }
}

//Função de alta ordem

@Composable
fun AppLogin(
    erro: String? = null,
    onErroConsumido: () -> Unit = {},
    showProgressBar: Boolean = false,
    onClickLogin: (String, String) -> Unit = { _, _ -> },
    onCreateLogin: (String, String) -> Unit = { _, _ -> }
) {

    val snackbarHostState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }


    LaunchedEffect(erro) {
        erro?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            onErroConsumido()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .imePadding(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),


            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .background(
                        color = Color(0xFF6EBDFA),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        1.dp, Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)

            ) {
                Text("Todo list")

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    maxLines = 1
                    )



                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    value = senha,
                    onValueChange = { senha = it },
                    placeholder = { Text("Senha") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (senhaVisivel) KeyboardType.Text else KeyboardType.Password
                    ),
                    visualTransformation = if (senhaVisivel)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                            Icon(
                                imageVector = if (senhaVisivel)
                                    Icons.Default.Visibility
                                else
                                    Icons.Default.VisibilityOff,
                                contentDescription = if (senhaVisivel)
                                    "Ocultar senha"
                                else
                                    "Mostrar senha"
                            )
                        }
                    },
                    singleLine = true,
                    maxLines = 1
                )
                if (senha.length < 6 && senha.isNotEmpty()) {
                    Text(
                        text = "A senha deve ter pelo menos 6 caracteres",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        color = Color.Red
                    )
                }

                Button(
                    onClick = {
                        onCreateLogin(email, senha)
                    }
                ) {
                    Text("Cadastrar")
                }
                Button(
                    onClick = {
                        onClickLogin(email, senha)
                    }
                ) { Text("Logar") }
            }
            //marlos.maur@gmail.com
            if (showProgressBar) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}