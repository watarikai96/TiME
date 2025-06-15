package com.time.android.ui.components.reusables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun FrequencyInputDialog(
    initial: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    label: String,
    validate: (Int) -> Boolean = { it > 0 }
) {
    var value by remember { mutableStateOf(initial.toString()) }
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(label) },
        text = {
            Column {
                TextField(
                    value = value,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.toIntOrNull() != null) {
                            value = newValue
                            isError = newValue.toIntOrNull()?.let { !validate(it) } ?: false
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text("Invalid value")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val intValue = value.toIntOrNull() ?: initial
                    if (validate(intValue)) {
                        onConfirm(intValue)
                    } else {
                        isError = true
                    }
                },
                enabled = value.toIntOrNull()?.let { validate(it) } ?: false
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
