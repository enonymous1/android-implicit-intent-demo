package com.enonymous1.implicitintentdemo

import android.content.Intent

/**
 * Thin Intent/Bundle glue for [PersonContract]. Kept separate from the
 * contract itself so the pure rules stay JVM-testable.
 *
 * Reading extras through [android.os.Bundle.get] is deprecated on newer
 * SDKs, but it is the only read that accepts an Int, a Long, or a String
 * age from an unknown caller without guessing. The value is normalised
 * through [personFromValues], so a caller passing the age as text still
 * works and a missing age is null, never a silent zero.
 */
@Suppress("DEPRECATION")
fun Intent.personOrNull(): Person? {
    val extras = extras ?: return null
    val firstName = extras.getString(PersonContract.EXTRA_FIRST_NAME)
        ?: extras.getString(PersonContract.LEGACY_EXTRA_FIRST_NAME)
    val lastName = extras.getString(PersonContract.EXTRA_LAST_NAME)
        ?: extras.getString(PersonContract.LEGACY_EXTRA_LAST_NAME)
    val ageValue = extras.get(PersonContract.EXTRA_AGE)
        ?: extras.get(PersonContract.LEGACY_EXTRA_AGE)
    val age = when (ageValue) {
        is Number -> ageValue.toInt()
        is String -> ageValue.trim().toIntOrNull()
        else -> null
    }
    return personFromValues(firstName, lastName, age)
}

/** Returns a result intent carrying [person] on the current contract names. */
fun Intent.withPerson(person: Person): Intent = apply {
    putExtra(PersonContract.EXTRA_FIRST_NAME, person.firstName)
    putExtra(PersonContract.EXTRA_LAST_NAME, person.lastName)
    putExtra(PersonContract.EXTRA_AGE, person.age)
}
