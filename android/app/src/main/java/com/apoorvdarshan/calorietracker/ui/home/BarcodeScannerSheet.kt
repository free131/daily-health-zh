package com.apoorvdarshan.calorietracker.ui.home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
@Composable
fun BarcodeScannerSheet(onBarcode: (String) -> Unit, onDismiss: () -> Unit) {
    LaunchedEffect(Unit) { onDismiss() }
}
