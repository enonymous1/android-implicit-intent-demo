package com.enonymous1.implicitintentdemo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonContractTest {

    @Test
    fun validInput_trimsNamesAndParsesAge() {
        assertEquals(
            PersonParse.Valid(Person("Ada", "Lovelace", 36)),
            parsePerson("  Ada ", " Lovelace ", " 36 "),
        )
    }

    @Test
    fun emptyFirstName_isInvalid() {
        assertEquals(
            PersonParse.Invalid("Enter a first name."),
            parsePerson("   ", "Lovelace", "36"),
        )
    }

    @Test
    fun emptyLastName_isInvalid() {
        assertEquals(
            PersonParse.Invalid("Enter a last name."),
            parsePerson("Ada", "", "36"),
        )
    }

    @Test
    fun emptyAge_isInvalid() {
        assertEquals(
            PersonParse.Invalid("Enter an age."),
            parsePerson("Ada", "Lovelace", "  "),
        )
    }

    @Test
    fun nonNumericAge_isInvalid() {
        assertTrue(parsePerson("Ada", "Lovelace", "thirty") is PersonParse.Invalid)
    }

    @Test
    fun negativeAge_isInvalid() {
        // The UI filters to digits, but the parser must not trust it.
        assertTrue(parsePerson("Ada", "Lovelace", "-1") is PersonParse.Invalid)
    }

    @Test
    fun overflowingAge_isInvalid() {
        // The original Integer.parseInt threw here; this must not.
        assertTrue(
            parsePerson("Ada", "Lovelace", "99999999999999999999") is PersonParse.Invalid,
        )
    }

    @Test
    fun ageRange_isEnforced() {
        assertEquals(
            PersonParse.Valid(Person("Ada", "Lovelace", 0)),
            parsePerson("Ada", "Lovelace", "0"),
        )
        assertEquals(
            PersonParse.Valid(Person("Ada", "Lovelace", 150)),
            parsePerson("Ada", "Lovelace", "150"),
        )
        assertTrue(parsePerson("Ada", "Lovelace", "151") is PersonParse.Invalid)
    }

    @Test
    fun personFromValues_requiresAllFields() {
        assertEquals(Person("Ada", "Lovelace", 36), personFromValues("Ada", "Lovelace", 36))
        assertNull(personFromValues("", "Lovelace", 36))
        assertNull(personFromValues("Ada", "  ", 36))
        assertNull(personFromValues("Ada", "Lovelace", null))
        assertNull(personFromValues("Ada", "Lovelace", 151))
    }

    @Test
    fun personFromValues_trimsNames() {
        assertEquals(Person("Ada", "Lovelace", 36), personFromValues(" Ada ", " Lovelace ", 36))
    }

    @Test
    fun addAction_matchesCurrentAndLegacyOnly() {
        assertTrue(PersonContract.isAddAction(PersonContract.ACTION_ADD_PERSON))
        assertTrue(PersonContract.isAddAction(PersonContract.LEGACY_ACTION_ADD_PERSON))
        assertFalse(PersonContract.isAddAction(PersonContract.ACTION_VIEW_PERSON))
        assertFalse(PersonContract.isAddAction(null))
        assertFalse(PersonContract.isAddAction("android.intent.action.MAIN"))
    }

    @Test
    fun viewAction_matchesCurrentAndLegacyOnly() {
        assertTrue(PersonContract.isViewAction(PersonContract.ACTION_VIEW_PERSON))
        assertTrue(PersonContract.isViewAction(PersonContract.LEGACY_ACTION_VIEW_PERSON))
        assertFalse(PersonContract.isViewAction(PersonContract.ACTION_ADD_PERSON))
        assertFalse(PersonContract.isViewAction(null))
    }
}
