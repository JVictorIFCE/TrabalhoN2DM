package com.example.trabalhofinaln2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trabalhofinaln2.ui.theme.TrabalhoFinalN2Theme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


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
                    tarefas = mainViewModel.listTask,
                    clickStatus = { tarefa ->
                        mainViewModel.status(tarefa)
                    }
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
    clickStatus: (Task) -> Unit = {},
    tarefas: List<Task> = emptyList()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var texto by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val pendentes = tarefas.filter { !it.checked }
    val concluidas = tarefas.filter { it.checked }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text("Seja bem vindo(a)")
                },
                actions = {
                    IconButton(
                        onClick = { showLogoutDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Sair"
                        )
                    }
                }
            )
        },

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Nova tarefa") },
                    modifier = Modifier.weight(1f),
                    trailingIcon = {
                        IconButton(onClick = { texto = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpar")
                        }
                    }
                )
                Spacer(modifier = Modifier.width(6.dp))


                // Botão adicionar
                Button(
                    onClick = {
                        if (texto.isNotBlank()) {
                            clickAdd(texto)
                            texto = ""
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Digite uma tarefa antes de adicionar",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Adicionar")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de lembretes
            LazyColumn {
                item {
                    if (pendentes.isNotEmpty()) {
                        Text("Tarefas pendentes")
                    } else {
                        Text("Nenhuma tarefa pendente")
                    }
                }
                for (pendente in pendentes) {
                    item {
                        Column {
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
                                        pendente.texto,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Excluir",
                                        modifier = Modifier
                                            .clickable {
                                                taskToDelete = pendente
                                                showDeleteDialog = true
                                            }
                                            .padding(start = 8.dp)
                                    )
                                    Checkbox(
                                        checked = pendente.checked,
                                        onCheckedChange = {
                                            clickStatus(pendente)
                                        }
                                    )

                                }
                            }

                        }
                    }
                }
                item {
                    if (concluidas.isNotEmpty()) {
                        Text("Tarefas Concluídas")

                    } else {
                        Text("Nenhuma tarefa concluída")
                    }
                }
                for (concluida in concluidas) {
                    item {
                        Column {
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
                                        concluida.texto,
                                        modifier = Modifier.weight(1f),
                                        textDecoration = TextDecoration.LineThrough
                                    )

                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Excluir",
                                        modifier = Modifier
                                            .clickable {
                                                taskToDelete = concluida
                                                showDeleteDialog = true
                                            }
                                            .padding(start = 8.dp)
                                    )
                                    Checkbox(
                                        checked = concluida.checked,
                                        onCheckedChange = {
                                            clickStatus(concluida)
                                        }
                                    )

                                }
                            }
                        }
                    }
                }

            }

        }
    }
    if (showDeleteDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                taskToDelete = null
            },
            title = {
                Text("Excluir tarefa")
            },
            text = {
                Text("Tem certeza que deseja excluir esta tarefa?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        clickDel(taskToDelete!!)
                        showDeleteDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        taskToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text("Sair da conta")
            },
            text = {
                Text("Deseja realmente sair?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        showLogoutDialog = false
                    }
                ) {
                    Text("Sair")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

}


@Preview(showBackground = true)
@Composable
fun Preview() {
    TrabalhoFinalN2Theme {
        App()
    }
}