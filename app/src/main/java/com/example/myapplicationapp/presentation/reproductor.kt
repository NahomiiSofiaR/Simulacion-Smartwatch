package com.example.myapplicationapp.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.myapplicationapp.R
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.clip




@Composable
fun MusicPlayerScreen(navController: NavController) {
    val context = LocalContext.current

    // Lista mutable que puede ser reordenada
    val initialSongs = remember {
        mutableStateListOf(
            Triple("Circles", "Post Malone", R.raw.circles),
            Triple("Lost in the Fire", "The Weeknd", R.raw.lost_in_the_fire),
            Triple("Shape of You", "Ed Sheeran", R.raw.shape_of_you),

        )
    }

    var index by remember { mutableStateOf(0) }
    var playing by remember { mutableStateOf(false) }
    val (title, artist, resId) = initialSongs[index]

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentPosition by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    LaunchedEffect(index) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, resId)
        duration = mediaPlayer?.duration ?: 0
        currentPosition = 0
        if (playing) mediaPlayer?.start()
        listState.animateScrollToItem(2)
    }

    LaunchedEffect(playing) {
        while (playing) {
            delay(1000)
            currentPosition = mediaPlayer?.currentPosition ?: 0
            if (duration > 0 && currentPosition >= duration) {
                index = (index + 1) % initialSongs.size
            }
        }
    }

    DisposableEffect(Unit) { onDispose { mediaPlayer?.release() } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(artist, color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(12.dp))
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = { currentPosition = it.toInt() },
                    onValueChangeFinished = { mediaPlayer?.seekTo(currentPosition) },
                    valueRange = 0f..(duration.toFloat().coerceAtLeast(1f)),
                    modifier = Modifier.fillMaxWidth(0.9f)
                )
                Row(modifier = Modifier.fillMaxWidth(0.9f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(formatTime(currentPosition), color = Color.White, fontSize = 12.sp)
                    Text(formatTime(duration), color = Color.White, fontSize = 12.sp)
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    mediaButton(Icons.Filled.SkipPrevious) {
                        index = if (index == 0) initialSongs.lastIndex else index - 1
                        playing = true
                    }
                    mediaButton(if (playing) Icons.Filled.Pause else Icons.Filled.PlayArrow) {
                        if (playing) mediaPlayer?.pause() else mediaPlayer?.start()
                        playing = !playing
                    }
                    mediaButton(Icons.Filled.SkipNext) {
                        index = (index + 1) % initialSongs.size
                        playing = true
                    }
                }
            }
        }
        item {
            Text("Tu lista de música", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        itemsIndexed(initialSongs) { i, (songTitle, songArtist, _) ->
            val isCurrent = i == index
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (i != 0) {
                            val selectedSong = initialSongs.removeAt(i)
                            initialSongs.add(0, selectedSong)
                            index = 0
                        }
                        playing = true
                    }
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        songTitle,
                        color = if (isCurrent) Color.Magenta else Color.White,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 16.sp
                    )
                    Text(songArtist, color = Color.Gray, fontSize = 13.sp)
                }
                if (isCurrent && playing) {
                    Icon(Icons.Filled.PlayArrow, null, tint = Color.Magenta)
                }
            }
        }
    }
}

@Composable
fun mediaButton(icon: ImageVector, onClick: () -> Unit) {
    Box(
        Modifier
            .size(60.dp)
            .border(2.dp, Color.Magenta, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(30.dp))
    }
}

fun formatTime(ms: Int): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}