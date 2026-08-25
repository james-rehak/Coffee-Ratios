package com.jam.coffeeratios

import org.junit.Assert.assertEquals
import org.junit.Test

class BrewStateTest {

  @Test
  fun `editing coffee recalculates water at the same ratio`() {
    val state = BrewState().edit(BrewField.COFFEE, "18")
    assertEquals("18", state.coffee)
    assertEquals("288", state.water)
    assertEquals("16", state.ratio)
  }

  @Test
  fun `editing water recalculates coffee at the same ratio`() {
    val state = BrewState().edit(BrewField.WATER, "500")
    assertEquals("31.3", state.coffee)
    assertEquals("500", state.water)
    assertEquals("16", state.ratio)
  }

  @Test
  fun `ratio never moves however many amounts are edited`() {
    val state = BrewState()
      .edit(BrewField.COFFEE, "18")
      .edit(BrewField.WATER, "300")
      .edit(BrewField.COFFEE, "22")
      .edit(BrewField.WATER, "1000")
    assertEquals("16", state.ratio)
    assertEquals("62.5", state.coffee)
    assertEquals("1000", state.water)
  }

  @Test
  fun `editing ratio recalculates water when coffee was set last`() {
    val state = BrewState()
      .edit(BrewField.COFFEE, "15")
      .edit(BrewField.RATIO, "17")
    assertEquals("15", state.coffee)
    assertEquals("255", state.water)
  }

  @Test
  fun `editing ratio recalculates coffee when water was set last`() {
    val state = BrewState()
      .edit(BrewField.WATER, "600")
      .edit(BrewField.RATIO, "15")
    assertEquals("600", state.water)
    assertEquals("40", state.coffee)
  }

  @Test
  fun `repeated ratio edits keep holding the same amount`() {
    val state = BrewState()
      .edit(BrewField.WATER, "600")
      .edit(BrewField.RATIO, "15")
      .edit(BrewField.RATIO, "12")
    assertEquals("600", state.water)
    assertEquals("50", state.coffee)
  }

  @Test
  fun `blank input leaves the other fields untouched`() {
    val state = BrewState().edit(BrewField.COFFEE, "")
    assertEquals("", state.coffee)
    assertEquals("320", state.water)
    assertEquals("16", state.ratio)
  }

  @Test
  fun `zero ratio leaves the amounts untouched`() {
    val state = BrewState().edit(BrewField.RATIO, "0")
    assertEquals("0", state.ratio)
    assertEquals("20", state.coffee)
    assertEquals("320", state.water)
  }

  @Test
  fun `zero coffee gives zero water`() {
    val state = BrewState().edit(BrewField.COFFEE, "0")
    assertEquals("0", state.water)
    assertEquals("16", state.ratio)
  }

  @Test
  fun `input is limited to digits and a single decimal point`() {
    assertEquals("16.52", sanitize("1a6.5.2x"))
    assertEquals("", sanitize("abc"))
    assertEquals(".5", sanitize(",5"))
  }

  @Test
  fun `partial decimal entry is preserved`() {
    val state = BrewState().edit(BrewField.RATIO, "16.")
    assertEquals("16.", state.ratio)
    assertEquals("320", state.water)
  }
}
