package com.example.trabalhofinaln2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trabalhofinaln2.ui.theme.TrabalhoFinalN2Theme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var authListener: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {

        authListener = FirebaseAuth.AuthStateListener { localAuth ->
            val localUser = localAuth.currentUser

            if (localUser == null) {
                Log.i("###", "Usuário não logado!")
                startLoginActivity()
            } else {
                Log.i("###", "Usuário logado: ${localUser.email}")

                mainViewModel.updateQuery(localUser.uid)
                mainViewModel.connect()
            }
        }

        FirebaseAuth.getInstance().addAuthStateListener(authListener)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TrabalhoFinalN2Theme {
                App(
                    clickAdd = { texto ->
                        mainViewModel.add(texto)
                    },
                    clickDel = { tarefa ->
                        mainViewModel.del(tarefa)
                    },
                    tarefas = mainViewModel.listTask
                )
            }
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK + Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        super.onDestroy()
    }
}


@Composable
fun App(
    clickAdd: (String) -> Unit = {},
    clickDel: (Task) -> Unit = {},
    tarefas: List<Task> = emptyList()
) {
    var texto by remember { mutableStateOf("") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Campo de texto
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text("Nova tarefa") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão adicionar
            Button(onClick = { clickAdd(texto) }) {
                Text("Adicionar")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão sair
            Button(onClick = { FirebaseAuth.getInstance().signOut() }) {
                Text("Sair")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de lembretes
            Column {
                for (tarefa in tarefas) {
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                tarefa.texto,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Excluir",
                                modifier = Modifier
                                    .clickable { clickDel(tarefa) }
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    TrabalhoFinalN2Theme {
        App()
    }
}
