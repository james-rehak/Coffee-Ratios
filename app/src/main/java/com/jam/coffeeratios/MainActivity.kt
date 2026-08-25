package com.jam.coffeeratios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jam.coffeeratios.ui.theme.CoffeeRatiosTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      CoffeeRatiosTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          RatioCalculator(modifier = Modifier.padding(innerPadding))
        }
      }
    }
  }
}

private val BrewStateSaver = listSaver<BrewState, String>(
  save = { listOf(it.ratio, it.coffee, it.water, it.pinned.name) },
  restore = { BrewState(it[0], it[1], it[2], BrewField.valueOf(it[3])) },
)

@Composable
fun RatioCalculator(modifier: Modifier = Modifier) {
  var state by rememberSaveable(stateSaver = BrewStateSaver) { mutableStateOf(BrewState()) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 24.dp, vertical = 32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(20.dp),
  ) {
    Column(
      modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Text("Coffee Ratio", style = MaterialTheme.typography.headlineMedium)
      Text(
        "Water to coffee by weight. Set the ratio, then edit either amount.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }

    BrewInput(
      label = "Ratio",
      value = state.ratio,
      prefix = "1 :",
      imeAction = ImeAction.Next,
      onValueChange = { state = state.edit(BrewField.RATIO, it) },
    )
    BrewInput(
      label = "Coffee",
      value = state.coffee,
      suffix = "g",
      isComputed = state.computed == BrewField.COFFEE,
      imeAction = ImeAction.Next,
      onValueChange = { state = state.edit(BrewField.COFFEE, it) },
    )
    BrewInput(
      label = "Water",
      value = state.water,
      suffix = "g",
      isComputed = state.computed == BrewField.WATER,
      imeAction = ImeAction.Done,
      onValueChange = { state = state.edit(BrewField.WATER, it) },
    )
  }
}

@Composable
private fun BrewInput(
  label: String,
  value: String,
  imeAction: ImeAction,
  onValueChange: (String) -> Unit,
  isComputed: Boolean = false,
  prefix: String? = null,
  suffix: String? = null,
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth(),
    label = { Text(label) },
    prefix = prefix?.let { { Text(it) } },
    suffix = suffix?.let { { Text(it) } },
    supportingText = if (isComputed) {
      { Text("calculated at the current ratio") }
    } else {
      null
    },
    singleLine = true,
    textStyle = MaterialTheme.typography.headlineSmall,
    keyboardOptions = KeyboardOptions(
      keyboardType = KeyboardType.Decimal,
      imeAction = imeAction,
    ),
  )
}

@Preview(showBackground = true)
@Composable
private fun RatioCalculatorPreview() {
  CoffeeRatiosTheme {
    RatioCalculator()
  }
}
