package com.example.napping

import android.annotation.SuppressLint
import android.health.connect.datatypes.HeartRateRecord
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.napping.ui.theme.NappingTheme
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NappingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF160059)
                ) {
                    TimerWatch()
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun TimerWatch() {
    var time by remember { mutableLongStateOf(0L) }

    var enabledTimer by remember { mutableStateOf(true) }

    var isRunning by remember {  mutableStateOf(false) }

    var isStopped by remember { mutableStateOf(false) }

    var startTime by remember { mutableLongStateOf(0L) }

    var heartRate by remember { mutableIntStateOf(80) }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.heartbeat),
                tint = Color.Red,
                contentDescription = "Heartbeat Icon",
                modifier = Modifier.padding(end=10.dp)
            )
            Text(
                text = String.format("%d bpm", heartRate),
                style = TextStyle(
                    color = Color.Red,
                    fontSize = 25.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        SleepStage(heartRate = heartRate)
        Spacer(modifier = Modifier.height(18.dp))
        Button (
            onClick = {
                if(isStopped) {
                    time = 0
                }
                if(enabledTimer) {
                    enabledTimer = false
                    isRunning = true
                    startTime = System.currentTimeMillis()
                }
            },
            shape = CircleShape,
            border = BorderStroke(width = 2.dp, Color.Black),
            modifier = Modifier
                .size(300.dp)
        ) {
            Text(
                text = formatTime(mils = time),
                style = MaterialTheme.typography.headlineLarge
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row {
            Button(
                onClick = {
                    if(isRunning) {
                        isRunning = false
                        enabledTimer = true
                        isStopped = true
                    }
                },
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.stop),
                    contentDescription = "Stop Icon",
                )
            }
        }
    }

    LaunchedEffect(isRunning) {
        while(isRunning) {
            delay(1000)
            time = System.currentTimeMillis() - startTime
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTime(mils: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(mils)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(mils) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(mils) % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@Composable
fun SleepStage(heartRate: Int) {
    val stage by remember { mutableStateOf(getStageName(heartRate)) }

    Text(
        text = stage,
        style = TextStyle(
            color = getColor(stage),
            fontSize = 30.sp
        )
    )
}

fun getStageName(heartRate: Int): String {
    var stage: String = "Light Sleep"
    if(heartRate < 70) stage = "Deep Sleep"
    if(heartRate in 71..79) stage = "REM"
    return stage
}

fun getColor(stage: String): Color {
    return when(stage) {
        "Light Sleep" -> Color.Yellow
        "Deep Sleep" -> Color.White
        "REM" -> Color.Green
        else -> Color.Black
    }
}

