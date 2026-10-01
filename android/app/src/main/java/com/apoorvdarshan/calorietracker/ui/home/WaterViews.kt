package com.apoorvdarshan.calorietracker.ui.home

import com.apoorvdarshan.calorietracker.models.UserNumberInput
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apoorvdarshan.calorietracker.R
import com.apoorvdarshan.calorietracker.ui.theme.AppColors
import com.apoorvdarshan.calorietracker.models.WaterUnit
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterCustomAmountSheet(unit: WaterUnit, onDismiss: () -> Unit, onAdd: (Int) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var customAmount by remember { mutableStateOf("") }
    val amountMl = UserNumberInput.decimal(customAmount)
        ?.takeIf { it > 0 && it <= unit.displayAmount(Int.MAX_VALUE) }
        ?.let(unit::toMilliliters)
        ?.takeIf { it > 0 }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.water_log_title), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.padding(horizontal = 31.dp))
            }

            Text(stringResource(R.string.water_how_much), fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

            OutlinedTextField(
                value = customAmount,
                onValueChange = { value ->
                    customAmount = UserNumberInput.decimalEdit(value, customAmount)
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.water_custom_amount)) },
                suffix = { Text(unit.symbol) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Button(
                onClick = {
                    amountMl?.let(onAdd)
                    onDismiss()
                },
                enabled = amountMl != null,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Calorie)
            ) {
                Icon(Icons.Filled.WaterDrop, contentDescription = null)
                Text(stringResource(R.string.water_add), modifier = Modifier.padding(start = 8.dp), fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
