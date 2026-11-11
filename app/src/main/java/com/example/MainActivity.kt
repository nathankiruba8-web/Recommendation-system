package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.LifeSyncApp
import com.example.ui.theme.LifeSyncTheme
import com.example.ui.viewmodel.LifeSyncViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LifeSyncViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LifeSyncTheme {
                LifeSyncApp(viewModel = viewModel)
            }
        }
    }
}
