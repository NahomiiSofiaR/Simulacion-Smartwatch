package com.example.myapplicationapp.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AlarmaScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("alarma_prefs", Context.MODE_PRIVATE)

    // Estado inicial con la hora guardada o valores por defecto
    var hour by remember { mutableStateOf(prefs.getInt("alarm_hour", 6)) }
    var minute by remember { mutableStateOf(prefs.getInt("alarm_minute", 0)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Configurar Alarma",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // Selector de hora
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            NumberPicker(
                value = hour,
                range = 0..23,
                onValueChange = { hour = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(":", fontSize = 28.sp, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            NumberPicker(
                value = minute,
                range = 0..59,
                onValueChange = { minute = it }
            )
        }

        // Botón para guardar
        Button(
            onClick = {
                prefs.edit()
                    .putInt("alarm_hour", hour)
                    .putInt("alarm_minute", minute)
                    .apply()
                navController.popBackStack() // Volver al reloj
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFe39ba2))
        ) {
            Text(text = "Guardar", color = Color.White)
        }
    }
}

/**
 * Pequeño NumberPicker para Wear OS (incrementa/decrementa con botones)
 */
@Composable
fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = {
                val newValue = if (value < range.last) value + 1 else range.first
                onValueChange(newValue)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("+", color = Color.White)
        }
        Text(
            text = value.toString().padStart(2, '0'),
            fontSize = 28.sp,
            color = Color.White
        )
        Button(
            onClick = {
                val newValue = if (value > range.first) value - 1 else range.last
                onValueChange(newValue)
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
        ) {
            Text("-", color = Color.White)
        }
    }
}