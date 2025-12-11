package com.example.trabalhofinaln2

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class MainViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()
    val listTask = mutableStateListOf<Task>()

    var query = db.collection("nothing")

    fun connect() {
        query.addSnapshotListener(observador)
    }

    fun updateQuery(userId: String) {
        query = db.collection("usuarios").document(userId).collection("tarefas")
    }

    val observador = EventListener<QuerySnapshot?> { event, error ->

        //se houver erro na leitura IGNORE
        if (error != null) return@EventListener

        event?.documentChanges?.forEach { change -> //tratar eventos de leitura do banco

            val tarefa = change.document.toObject(Task::class.java)

            Log.i("###", "L: $tarefa")

            when (change.type) {
                DocumentChange.Type.ADDED -> {
                    listTask.add(tarefa)
                }

                DocumentChange.Type.MODIFIED -> {
                    listTask.set(change.newIndex, tarefa)
                }

                DocumentChange.Type.REMOVED -> {
                    listTask.removeAt(change.oldIndex)
                }
            }
        }
    }

    fun del(tarefa: Task) {
        query.document(tarefa.id)
            .delete()
            .addOnCompleteListener { task ->  //opcional
                if (task.isSuccessful) {
                    Log.i("###", "documento deletado!")
                } else {
                    Log.e("###", "erro", task.exception)
                }
            }
    }

    fun add(texto: String) {
        val refDoc = query.document()
        val tarefa = Task(refDoc.id, texto)

        refDoc.set(tarefa).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("###", "documento salvo!")
            } else {
                Log.e("###", "erro", task.exception)
            }
        }
    }

    fun status(tarefa: Task) {
        query.document(tarefa.id)
            .update("checked", !tarefa.checked)
            .addOnCompleteListener { task -> //opcional
                if (task.isSuccessful) {
                    Log.i("###", "documento atualizado!")
                } else {
                    Log.e("###", "erro", task.exception)
                }
            }
    }
}