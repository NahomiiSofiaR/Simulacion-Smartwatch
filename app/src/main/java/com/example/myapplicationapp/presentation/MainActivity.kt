package com.example.myapplicationapp.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.*
import com.example.myapplicationapp.presentation.theme.MyApplicationAppTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.pow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import com.example.myapplicationapp.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import java.text.SimpleDateFormat
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavController
import androidx.compose.foundation.lazy.LazyRow
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import android.media.MediaPlayer
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import android.app.NotificationChannel
import android.app.NotificationManager


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationAppTheme {
                val navController = rememberNavController()


                // Crear canal de notificación al iniciar
                createNotificationChannel(this)

                // Pedir permiso si es Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                    }
                }

                // Si la app se abre por la notificación, navega a "alarma"
                LaunchedEffect(Unit) {
                    val navTo = intent?.getStringExtra("navigateTo")
                    if (navTo == "alarma") {
                        navController.navigate("alarma")
                    }
                }
                NavHost(navController = navController, startDestination = "watchFace") {
                    composable("watchFace") { WatchFaceScreen(navController) }
                    composable("menu") { MenuScreen(navController) }
                    composable("pasos") { PasosScreen() }
                    composable("calculadora") { Calculadora() }
                    composable("mensajes") { MensajesScreen() }
                    composable("music") { MusicScreen() }
                    composable("workout") { WorkoutScreen() }
                    composable("yoga") { YogaScreen() }
                    composable("ciclo") { CicloScreen() }
                    composable("flappy") {
                        FlappyBirdScreen(onBack = { navController.popBackStack() })
                    }
                    composable("Descanzo") {
                        EyeRestScreen(navController = navController) // Usa tu función
                    }
                    composable("reproductor") {
                        MusicPlayerScreen(navController = navController)
                    }
                    composable("alarma") {
                        AlarmaScreen(navController = navController)
                    }



                }
            }
        }
    }
}

// Crear canal de notificación
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "alarm_channel",
            "Alarma",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones de alarma"
        }
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }
}

@Composable
fun WatchFaceScreen(navController: NavHostController) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("alarma_prefs", Context.MODE_PRIVATE)
    val alarmHour = prefs.getInt("alarm_hour", -1)
    val alarmMinute = prefs.getInt("alarm_minute", -1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { navController.navigate("menu") },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (alarmHour != -1 && alarmMinute != -1) {
                TimeDisplayWithAlarm(context, alarmHour, alarmMinute)
            } else {
                TimeDisplay()
            }

            DateDisplay()
            Spacer(modifier = Modifier.height(12.dp))
            IconStatsRow()
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun TimeDisplay() {
    val zoneId = ZoneId.of("America/Mexico_City")
    var currentTime by remember { mutableStateOf(ZonedDateTime.now(zoneId)) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = ZonedDateTime.now(zoneId)
            delay(60_000L)
        }
    }

    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeString = currentTime.format(formatter)

    Text(
        text = timeString,
        fontSize = 56.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFFc85387),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DateDisplay() {
    val zoneId = ZoneId.of("America/Mexico_City")
    val currentDate = ZonedDateTime.now(zoneId).toLocalDate()
    val formatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale("es", "MX"))
    val dateString = currentDate.format(formatter)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = dateString,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = Color(0xFFcdcdcd),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "© Derechos R by NS",
            fontSize = 12.sp,
            color = Color(0xFFcdcdcd),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TimeDisplayWithAlarm(context: Context, alarmHour: Int, alarmMinute: Int) {
    val zoneId = ZoneId.of("America/Mexico_City")
    var currentTime by remember { mutableStateOf(ZonedDateTime.now(zoneId)) }
    var alarmTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = ZonedDateTime.now(zoneId)

            if (currentTime.hour == alarmHour &&
                currentTime.minute == alarmMinute &&
                !alarmTriggered
            ) {
                alarmTriggered = true
                playAlarmSound(context)
            }

            delay(1000L)
        }
    }

    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeString = currentTime.format(formatter)

    Text(
        text = timeString,
        fontSize = 56.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFFc85387),
        textAlign = TextAlign.Center
    )
}

fun playAlarmSound(context: Context) {
    val player = MediaPlayer.create(context, R.raw.alarma)
    player.start()
    player.setOnCompletionListener {
        it.release()
    }

    // Enviar notificación
    sendAlarmNotification(context)
}

private fun sendAlarmNotification(context: Context) {
    // ✅ Verificar permiso antes de enviar
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return // No hay permiso, salir sin notificar
        }
    }

    val builder = NotificationCompat.Builder(context, "alarm_channel")
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle("⏰ Alarma activada")
        .setContentText("Tu alarma está sonando ahora mismo")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setAutoCancel(true)

    with(NotificationManagerCompat.from(context)) {
        notify(1001, builder.build())
    }
}
@Composable
fun IconStatsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatIcon(Icons.Default.WbSunny, "Clima", Color(0xFFe39ba2))
        StatIcon(Icons.Default.DirectionsWalk, "Pasos", Color(0xFFc85387))
        StatIcon(Icons.Default.Favorite, "Cardio", Color(0xFFcdcdcd))
    }
}

@Composable
fun StatIcon(icon: ImageVector, contentDesc: String, tintColor: Color) {
    Icon(
        imageVector = icon,
        contentDescription = contentDesc,
        tint = tintColor,
        modifier = Modifier.size(32.dp)
    )
}
@Composable
fun MenuScreen(navController: NavHostController) {
    val apps = listOf(
        Pair("Pasos", Icons.Filled.DirectionsWalk),
        Pair("Calculadora", Icons.Filled.Calculate),
        Pair("Mensajes", Icons.Filled.Message),
        Pair("Musica", Icons.Filled.MusicNote),
        Pair("Entrenamiento", Icons.Default.DirectionsRun),
        Pair("Yoga", Icons.Default.AccessibilityNew),
        Pair("Ciclo", Icons.Filled.CalendarMonth),
        Pair("Estado de ánimo", Icons.Filled.Mood),
        Pair("Flappy Bird", Icons.Filled.SportsEsports),
        Pair("Descanso Visual", Icons.Filled.Visibility),
        Pair("Reproductor de Música", Icons.Filled.MusicNote),
        Pair("Alarma", Icons.Filled.Alarm),


    )

    val listState = remember { ScalingLazyListState() }
    val botonColor = Color(0xFFe39ba2)

    ScalingLazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black), // ← Aquí
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(apps) { (appName, icon) ->
            Button(
                onClick = {
                    if (appName == "Calculadora") {
                        navController.navigate("calculadora")
                    }
                    if (appName == "Pasos") {
                        navController.navigate("pasos")
                    }
                    if (appName == "Mensajes") {
                        navController.navigate("mensajes")
                    }
                    if (appName == "Musica") {
                        navController.navigate("music")
                    }
                    if (appName == "Entrenamiento") {
                        navController.navigate("workout")
                    }
                    if (appName == "Yoga") {
                        navController.navigate("yoga")
                    }
                    if (appName == "Ciclo") {
                        navController.navigate("ciclo")
                    }
                    if (appName == "Estado de ánimo") {
                        navController.navigate("paginaTres")
                    }
                    if (appName == "Flappy Bird") {
                        navController.navigate("flappy")
                    }
                    if (appName == "Descanso Visual") {
                        navController.navigate("Descanzo")
                    }
                    if (appName == "Reproductor de Música") {
                        navController.navigate("reproductor")
                    }
                    if (appName == "Alarma") {
                        navController.navigate("alarma")
                    }
                },
                modifier = Modifier
                    .width(140.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = botonColor,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = appName,
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = appName,
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}



@Composable
fun PasosScreen(pasos: Int = 6562, meta: Int = 8000) {
    val progreso = pasos.toFloat() / meta.toFloat()
    val rosaClaro = Color(0xFFe39ba2)
    val rosaOscuro = Color(0xFFc85387)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Aquí sí usamos el scope correctamente
        val canvasSize = minOf(this.maxWidth, this.maxHeight) * 0.75f

        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(canvasSize)) {
                drawArc(
                    color = rosaClaro.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )
                drawArc(
                    color = rosaClaro,
                    startAngle = -90f,
                    sweepAngle = 360f * progreso,
                    useCenter = false,
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Steps",
                    color = rosaClaro,
                    fontSize = 16.sp
                )
                Text(
                    text = pasos.toString(),
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "/ $meta",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}



@Composable
fun Calculadora() {
    var caja by remember { mutableStateOf("0") }
    var resultado by remember { mutableStateOf(0.0) }
    var operador by remember { mutableStateOf("") }
    var dato1 by remember { mutableStateOf(0.0) }
    var nuevoValor by remember { mutableStateOf(true) }

    fun presionarBoton(button: String) {
        when (button) {
            in "0".."9", "." -> {
                if (nuevoValor) {
                    caja = if (button == ".") "0." else button
                    nuevoValor = false
                } else {
                    if (button == "." && caja.contains(".")) return
                    caja += button
                }
            }

            "/", "*", "-", "+", "^" -> {
                operador = button
                dato1 = caja.toDoubleOrNull() ?: 0.0
                nuevoValor = true
            }

            "=" -> {
                val dato2 = caja.toDoubleOrNull() ?: 0.0
                resultado = when (operador) {
                    "/" -> dato1 / dato2
                    "*" -> dato1 * dato2
                    "-" -> dato1 - dato2
                    "+" -> dato1 + dato2
                    "^" -> dato1.pow(dato2)
                    else -> dato2
                }
                caja = resultado.toString()
                nuevoValor = true
            }

            "C" -> {
                caja = "0"
                operador = ""
                dato1 = 0.0
                resultado = 0.0
                nuevoValor = true
            }

            "+/-" -> {
                caja = (-1 * (caja.toDoubleOrNull() ?: 0.0)).toString()
            }

            "%" -> {
                caja = ((caja.toDoubleOrNull() ?: 0.0) / 100).toString()
            }

            "√" -> {
                caja = kotlin.math.sqrt(caja.toDoubleOrNull() ?: 0.0).toString()
            }
        }
    }

    androidx.wear.compose.material.MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black), // ← Aquí
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScalingLazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "Calculadora",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                item {
                    Text(
                        text = caja,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(8.dp),
                        color = Color.White
                    )
                }
                item { Renglones("7", "8", "9", "/", "C") { presionarBoton(it) } }
                item { Renglones("4", "5", "6", "*", "+/-") { presionarBoton(it) } }
                item { Renglones("1", "2", "3", "-", "√") { presionarBoton(it) } }
                item { Renglones("0", ".", "=", "+", "^") { presionarBoton(it) } } // aquí se agregó "^"
            }
        }
    }
}

@Composable
fun Renglones(vararg buttons: String, onClick: (String) -> Unit) {
    val miColor = Color(0xFFc85387) // color rosa

    Row(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        buttons.forEach { button ->
            Button(
                modifier = Modifier.size(48.dp),
                onClick = { onClick(button) },
                colors = androidx.wear.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = miColor,
                    contentColor = Color.White
                )
            ) {
                Text(text = button, fontSize = 16.sp)
            }
        }
    }
}



@Composable
fun MensajesScreen() {
    val nombre = "Jessica Robles"
    val mensaje = "Hola, soy Jessica!"
    val fecha = "Jueves • Ahora"

    Scaffold(
        timeText = { TimeText() },
        vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Fondo negro
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black) // Fondo negro también aquí
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen
            Image(
                painter = painterResource(id = R.drawable.img), // imagen en drawable
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFB89EFF), CircleShape) // Borde morado claro
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Nombre
            Text(
                text = nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White
            )

            // Fecha/hora
            Text(
                text = fecha,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Burbuja de mensaje
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(color = Color(0xFFc85387), shape = RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = mensaje,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun MusicScreen() {
    val rosa = Color(0xFFc85387)
    val rosaClaro = Color(0xFFe39ba2)
    val currentTime = ZonedDateTime.now(ZoneId.systemDefault()).toLocalTime()
        .format(DateTimeFormatter.ofPattern("HH:mm"))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hora
            Text(
                text = currentTime,
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            // Título
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Chef Table Radio",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Eli Kulp",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Botón de pausa circular
            Box(contentAlignment = Alignment.Center) {
                Canvas(modifier = Modifier.size(72.dp)) {
                    drawCircle(
                        color = rosa.copy(alpha = 0.4f),
                        radius = size.minDimension / 2
                    )
                    drawCircle(
                        color = rosa,
                        radius = size.minDimension / 2.2f,
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Controles inferiores
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Retroceder */ }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Anterior",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { /* Corazón */ }) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { /* Volumen */ }) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volumen",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { /* Siguiente */ }) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Siguiente",
                        tint = Color.White
                    )
                }
            }

            // Paginador (solo decorativo por ahora)
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.9f))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.4f))
                )
            }
        }
    }
}

@Composable
fun WorkoutScreen() {
    val rosa = Color(0xFFc85387)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
        ) {
            // Texto superior
            Text(
                text = "1 run this week",
                color = rosa,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            // Íconos de actividad
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActivityIcon(Icons.Default.DirectionsRun, rosa)
                ActivityIcon(Icons.Default.AccessibilityNew, rosa)
                ActivityIcon(Icons.Default.DirectionsBike, rosa)
            }

            // Botón "More"
            Button(
                onClick = { /* Acción para ver más */ },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = rosa,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .width(100.dp)
                    .height(36.dp)
            ) {
                Text(text = "More", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ActivityIcon(icon: ImageVector, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun YogaScreen() {
    val rosa = Color(0xFFc85387)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            // Título
            Text(
                text = "Power Yoga",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = rosa
            )

            // Botón "Start"
            Button(
                onClick = { /* Acción para iniciar */ },
                modifier = Modifier
                    .width(140.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = rosa,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = "Start",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Última sesión
            Text(
                text = "Last session 45m",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun CicloScreen() {
    val listState: ScalingLazyListState = rememberScalingLazyListState()

    var startDate by remember { mutableStateOf<Date?>(null) }
    var duration by remember { mutableStateOf(5) } // duración predeterminada
    val cycleLength = 28

    val selectedDates = remember(startDate, duration) {
        startDate?.let {
            (0 until duration).map { offset ->
                Calendar.getInstance().apply {
                    time = it
                    add(Calendar.DAY_OF_MONTH, offset)
                }.time
            } ?: emptyList()
        } ?: emptyList()
    }

    val ovulationDate = startDate?.let {
        Calendar.getInstance().apply {
            time = it
            add(Calendar.DAY_OF_MONTH, 14)
        }.time
    }

    val nextCycleDate = startDate?.let {
        Calendar.getInstance().apply {
            time = it
            add(Calendar.DAY_OF_MONTH, cycleLength)
        }.time
    }

    val currentMonth = remember { mutableStateOf(Calendar.getInstance()) }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentPadding = PaddingValues(8.dp),
        state = listState
    ) {
        item {
            Text(
                text = "Ciclo",
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFef9a9a))
                    .padding(12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Próximo ciclo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Hoy: ${SimpleDateFormat("d MMMM", Locale("es", "MX")).format(Date())}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFe39ba2))
                    .padding(12.dp)
            ) {
                CalendarMonthView(
                    selectedDates = selectedDates,
                    ovulationDate = ovulationDate,
                    nextCycleDate = nextCycleDate,
                    currentMonth = currentMonth.value,
                    onDateToggle = { date ->
                        startDate = date
                        val cal = Calendar.getInstance()
                        cal.time = date
                        currentMonth.value = cal
                    },
                    onMonthChange = { newMonth ->
                        currentMonth.value = newMonth
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            DurationPicker(
                duration = duration,
                onIncrease = { if (duration < 10) duration++ },
                onDecrease = { if (duration > 1) duration-- }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFc85387))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("es", "MX"))

                    Text(
                        text = if (startDate != null) "Inicio: ${dateFormat.format(startDate!!)}" else "Selecciona inicio del ciclo",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Duración: $duration días",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = ovulationDate?.let { "Ovulación: ${dateFormat.format(it)}" } ?: "Ovulación: -",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = nextCycleDate?.let { "Próximo ciclo: ${dateFormat.format(it)}" } ?: "Próximo ciclo: -",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun DurationPicker(duration: Int, onIncrease: () -> Unit, onDecrease: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFc85387), RoundedCornerShape(8.dp))
            .padding(vertical = 10.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onDecrease,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            modifier = Modifier.size(40.dp)
        ) {
            Text(text = "-", fontSize = 24.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = "Duración $duration",
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(24.dp))

        Button(
            onClick = onIncrease,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            modifier = Modifier.size(40.dp)
        ) {
            Text(text = "+", fontSize = 24.sp, color = Color.Black)
        }
    }
}

@Composable
fun CalendarMonthView(
    selectedDates: List<Date>,
    ovulationDate: Date?,
    nextCycleDate: Date?,
    currentMonth: Calendar,
    onDateToggle: (Date) -> Unit,
    onMonthChange: (Calendar) -> Unit
) {
    val monthStart = (currentMonth.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val daysInMonth = monthStart.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = monthStart.get(Calendar.DAY_OF_WEEK) - 1

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "< Anterior",
                modifier = Modifier.clickable {
                    val newMonth = (currentMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, -1)
                    }
                    onMonthChange(newMonth)
                },
                color = Color.White,
                fontSize = 12.sp
            )
            Text(
                text = SimpleDateFormat("MMMM yyyy", Locale("es", "MX")).format(currentMonth.time)
                    .replaceFirstChar { it.uppercase() },
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Siguiente >",
                modifier = Modifier.clickable {
                    val newMonth = (currentMonth.clone() as Calendar).apply {
                        add(Calendar.MONTH, 1)
                    }
                    onMonthChange(newMonth)
                },
                color = Color.White,
                fontSize = 12.sp
            )
        }

        Spacer(Modifier.height(4.dp))

        val rows = (firstDayOfWeek + daysInMonth + 6) / 7
        var dayCounter = 1 - firstDayOfWeek

        repeat(rows) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 0..6) {
                    if (dayCounter in 1..daysInMonth) {
                        val dayDate = (currentMonth.clone() as Calendar).apply {
                            set(Calendar.DAY_OF_MONTH, dayCounter)
                        }.time

                        val isSelected = selectedDates.any { sameDay(it, dayDate) }
                        val bgColor = when {
                            isSelected -> Color(0xFFc85387)
                            sameDay(dayDate, ovulationDate) -> Color(0xFFff8a80)
                            sameDay(dayDate, nextCycleDate) -> Color(0xFFa5d6a7)
                            else -> Color.Transparent
                        }

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(bgColor)
                                .clickable { onDateToggle(dayDate) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayCounter.toString(),
                                color = if (bgColor == Color.Transparent) Color.White else Color.Black,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(28.dp))
                    }
                    dayCounter++
                }
            }
        }
    }
}

fun sameDay(date1: Date?, date2: Date?): Boolean {
    if (date1 == null || date2 == null) return false
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}




