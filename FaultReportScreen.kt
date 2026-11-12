package com.uedcl.power.presentation.faults

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun FaultReportScreen(
    onSubmitClicked: (String) -> Unit
) {
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Report Grid Fault", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Fault Description") },
            modifier = Modifier.fillMaxWidth().testTag("fault_input_field")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSubmitClicked(description) },
            modifier = Modifier.fillMaxWidth().testTag("submit_fault_button")
        ) {
            Text("Submit Report")
        }
    }
}
