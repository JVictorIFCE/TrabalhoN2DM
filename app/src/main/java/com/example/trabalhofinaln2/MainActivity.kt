package com.example.trabalhofinaln2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.trabalhofinaln2.ui.theme.TrabalhoFinalN2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrabalhoFinalN2Theme {
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    App()
}
