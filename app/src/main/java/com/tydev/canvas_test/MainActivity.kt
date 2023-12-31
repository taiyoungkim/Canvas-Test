package com.tydev.canvas_test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colors = listOf(Color.Black, Color.Blue, Color.Green, Color.Yellow, Color.Red, Color.LightGray)

            Column {
                var selectedColor by remember { mutableStateOf(colors.first()) }
                Row(
                    modifier = Modifier
                        .background(Color.DarkGray)
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    colors.forEach {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(it)
                                .clickable {
                                    selectedColor = it
                                }
                        ) {}
                    }
                }
                ScribbleCanvas(selectedColor, 5.dp, Modifier.fillMaxSize())
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ScribbleCanvas(color: Color, lineWidth: Dp, modifier: Modifier = Modifier) {
        var paths by remember { mutableStateOf<List<List<Offset>>>(emptyList()) }
        var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

        Canvas(
            modifier = modifier
                .background(Color.Gray)
                .pointerInput(remember { MutableInteractionSource() }) {
                    detectDragGestures(
                        onDragStart = {
                            currentPath = listOf(it)
                        },
                        onDrag = { change, _ ->
                            val pointsFromHistory = change.historical.map { it.position }.toTypedArray()
                            val newPoints = listOf(*pointsFromHistory, change.position)
                            currentPath = currentPath + newPoints
                        },
                        onDragEnd = {
                            paths = paths + listOf(currentPath)
                            currentPath = emptyList()
                        }
                    )
                }
        ) {
            // Draw existing paths
            for (path in paths) {
                if (path.size > 1) {
                    drawPathFromPoints(path, color, lineWidth)
                }
            }

            // Draw current path
            if (currentPath.size > 1) {
                drawPathFromPoints(currentPath, color, lineWidth)
            }
        }
    }

    private fun DrawScope.drawPathFromPoints(points: List<Offset>, color: Color, lineWidth: Dp) {
        val path = Path().apply {
            val firstPoint = points.first()
            val rest = points.subList(1, points.size - 1)

            moveTo(firstPoint.x, firstPoint.y)
            rest.forEach {
                lineTo(it.x, it.y)
            }
        }

        drawPath(path = path, color = color, style = Stroke(width = lineWidth.toPx()))
    }
}
