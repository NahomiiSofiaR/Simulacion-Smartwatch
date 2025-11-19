package com.example.myapplicationapp.presentation

import android.media.RingtoneManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import kotlinx.coroutines.delay
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults


@Composable
fun EyeRestScreen(navController:NavController) {
    val context = LocalContext.current
    var selectedMinutes by remember { mutableStateOf(5) }
    var remainingTime by remember { mutableStateOf(selectedMinutes * 60) }
    var isRunning by remember { mutableStateOf(false) }

    val minutes = (remainingTime / 60).toString().padStart(2, '0')
    val seconds = (remainingTime % 60).toString().padStart(2, '0')
    val progress = remember(remainingTime, selectedMinutes) {
        if (selectedMinutes == 0) 0f else remainingTime / (selectedMinutes * 60f)
    }
    BackHandler(enabled = isRunning){}
    // Timer logic
    LaunchedEffect(isRunning, remainingTime) {
        if (isRunning && remainingTime > 0) {
            delay(1000)
            remainingTime -= 1
        }
        if (remainingTime == 0) {
            isRunning = false
            val notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notificationSound)
            r?.play()
            // Puedes añadir vibración aquí
        }
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item {
            Text(
                text = "Hora de descansar",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
            )
        }

        item {
            // Cronómetro con anillo de progreso
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable {
                        if (remainingTime > 0) {
                            isRunning = !isRunning
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Canvas para el anillo
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = Color.DarkGray,
                        radius = size.minDimension / 2
                    )
                    drawArc(
                        color = Color(0xFFc85387), // azul claro
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 10f, cap = StrokeCap.Round)
                    )
                }

                Text(
                    text = "$minutes:$seconds",
                    fontSize = 24.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(5, 10, 15).forEach { min ->
                    Button(
                        onClick = {
                            selectedMinutes = min
                            remainingTime = min * 60
                            isRunning = false
                        },
                        modifier = Modifier
                            .size(50.dp), // Tamaño fijo para el botón
                        contentPadding = PaddingValues(0.dp), // Elimina padding interno
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFc85387),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = "$min min",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .widthIn(max = 45.dp) // Asegura que el texto no exceda el botón
                                .background(Color.Transparent) // Debug: verifica espacio
                        )
                    }
                }
            }
        }
    }
}