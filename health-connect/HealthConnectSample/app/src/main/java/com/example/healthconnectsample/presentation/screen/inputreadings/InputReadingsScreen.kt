package com.example.healthconnectsample.presentation.screen.inputreadings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.metadata.Metadata
import androidx.health.connect.client.permission.HealthDataRequestPermissions
import androidx.health.connect.client.permission.Permission
import androidx.health.connect.client.records.Weight
import com.example.healthconnectsample.R
import com.example.healthconnectsample.data.dateTimeWithOffsetOrDefault
import com.example.healthconnectsample.presentation.theme.HealthConnectTheme
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

@Composable
fun InputReadingsScreen(
    permissions: Set<Permission>,
    permissionsGranted: Boolean,
    readingsList: List<Weight>,
    uiState: InputReadingsViewModel.UiState,
    onInsertClick: (Weight) -> Unit = {},
    onDeleteClick: (String) -> Unit = {},
    onError: (Throwable?) -> Unit = {},
    onPermissionsResult: () -> Unit = {},
    weeklyAvg: Double
) {

    val launcher = rememberLauncherForActivityResult(HealthDataRequestPermissions()) {
        onPermissionsResult()
    }

    // Remember the last error ID, such that it is possible to avoid re-launching the error
    // notification for the same error when the screen is recomposed, or configuration changes etc.
    val errorId = rememberSaveable { mutableStateOf(UUID.randomUUID()) }

    // The [InputReadingsScreenViewModel.UiState] provides details of whether the last action was a
    // success or resulted in an error. Where an error occurred, for example in reading and writing
    // to Health Connect, the user is notified, and where the error is one that can be recovered
    // from, an attempt to do so is made.

    LaunchedEffect(uiState) {
        if (uiState is InputReadingsViewModel.UiState.Error && errorId.value != uiState.uuid) {
            onError(uiState.exception)
            errorId.value = uiState.uuid
        }
    }

    var weightInput by remember { mutableStateOf("") }

    //Check if the input value is a valid weight
    fun hasValidDoubleInRange(weight: String): Boolean {
    val tempVal = weight.toDoubleOrNull()
        return if(tempVal==null){
            false
        } else tempVal <= 1000

    }


    if (uiState != InputReadingsViewModel.UiState.Loading) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!permissionsGranted) {
                item {
                    Button(
                        onClick = { launcher.launch(permissions) }
                    ) {
                        Text(text = stringResource(R.string.permissions_button_label))
                    }
                }
            } else {
                item {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = {
                            weightInput = it

                        },

                        label = {
                            Text(stringResource(id = R.string.weight_input))
                        },
                        isError = !hasValidDoubleInRange(weightInput),
                        keyboardActions = KeyboardActions { !hasValidDoubleInRange(weightInput) }
                    )
                    if (!hasValidDoubleInRange(weightInput)) {
                        Text(
                            text = stringResource(id = R.string.valid_weight_error_message),
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    Button(
                        enabled = hasValidDoubleInRange(weightInput),
                        onClick = {
                            onInsertClick(
                                Weight(
                                    weightInput.toDouble(),
                                    time = Instant.now(),
                                    zoneOffset = null,
                                    metadata = Metadata(UUID.randomUUID().toString())
                                )

                            )
                            //clear TextField when new weight is entered
                            weightInput = ""
                        },

                        modifier = Modifier.fillMaxHeight()

                    )
                    {
                        Text(text = stringResource(id = R.string.add_readings_button))
                    }

                    Text(
                        text = stringResource(id = R.string.previous_readings),
                        fontSize = 24.sp,
                        color = MaterialTheme.colors.primary
                    )

                }
                items(readingsList) { reading ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //show local date and time
                        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        val zonedDateTime =
                            dateTimeWithOffsetOrDefault(reading.time, reading.zoneOffset)
                        val uid = reading.metadata.uid
                        Text(
                            text = "${reading.weightKg}" + stringResource(id = R.string.kilograms),
                        )
                        Text(text = formatter.format(zonedDateTime))
                        IconButton(
                            onClick = {
                                if (uid != null) {
                                    onDeleteClick(uid)
                                }
                            },
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                stringResource(R.string.delete_button_readings)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(id = R.string.weekly_avg), fontSize = 24.sp,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier.padding(vertical = 20.dp)
                    )
                    Text(text = "$weeklyAvg".take(5) + stringResource(id = R.string.kilograms))

                }

            }

        }

    }
}

@Preview
@Composable
fun InputReadingsScreenPreview() {
    val inputTime = Instant.now()
    HealthConnectTheme(darkTheme = false) {
        InputReadingsScreen(
            permissions = setOf(),
            weeklyAvg = 54.5,
            permissionsGranted = true,
            readingsList = listOf(
                Weight(
                    54.0,
                    time = inputTime,
                    zoneOffset = null
                ),
                Weight(
                    55.0,
                    time = inputTime,
                    zoneOffset = null
                )
            ),

            uiState = InputReadingsViewModel.UiState.Done
        )
    }
}