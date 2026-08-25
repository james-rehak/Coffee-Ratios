package com.jam.coffeeratios

import java.util.Locale

/** The three editable quantities. Ratio is water per 1 part coffee, by weight. */
enum class BrewField { RATIO, COFFEE, WATER }

/**
 * Holds the raw text of all three fields.
 *
 * Ratio is the recipe: it changes only when you edit it directly. Editing either amount
 * recalculates the other one at the current ratio. Editing the ratio recalculates whichever
 * amount you touched less recently, holding the one you set last.
 */
data class BrewState(
  val ratio: String = "16",
  val coffee: String = "20",
  val water: String = "320",
  /** The amount held fixed when the ratio changes — whichever was edited most recently. */
  val pinned: BrewField = BrewField.COFFEE,
) {
  /** The amount that will be recalculated by the next edit to either other field. */
  val computed: BrewField
    get() = if (pinned == BrewField.WATER) BrewField.COFFEE else BrewField.WATER

  fun edit(field: BrewField, raw: String): BrewState {
    val text = sanitize(raw)
    return when (field) {
      BrewField.RATIO -> copy(ratio = text).let { it.recalculate(it.computed) }
      BrewField.COFFEE -> copy(coffee = text, pinned = BrewField.COFFEE).recalculate(BrewField.WATER)
      BrewField.WATER -> copy(water = text, pinned = BrewField.WATER).recalculate(BrewField.COFFEE)
    }
  }

  private fun recalculate(target: BrewField): BrewState {
    val r = ratio.toDoubleOrNull() ?: return this
    if (r <= 0) return this
    return when (target) {
      BrewField.COFFEE -> water.toDoubleOrNull()?.let { copy(coffee = format(it / r)) } ?: this
      BrewField.WATER -> coffee.toDoubleOrNull()?.let { copy(water = format(it * r)) } ?: this
      BrewField.RATIO -> this
    }
  }
}

/** Digits and at most one decimal point, so parsing never has to guess. */
internal fun sanitize(raw: String): String {
  val cleaned = raw.replace(',', '.').filter { it.isDigit() || it == '.' }
  val dot = cleaned.indexOf('.')
  val single =
    if (dot < 0) cleaned
    else cleaned.substring(0, dot + 1) + cleaned.substring(dot + 1).filter { it != '.' }
  return single.take(8)
}

/** One decimal place, trailing ".0" dropped — grams finer than 0.1 are noise on a home scale. */
internal fun format(value: Double): String {
  if (!value.isFinite() || value < 0) return ""
  return String.format(Locale.US, "%.1f", value).removeSuffix(".0")
}
