package com.enonymous1.implicitintentdemo

/**
 * The person a form or details screen carries, and the implicit-intent
 * contract it travels on.
 *
 * This file is deliberately free of Android types so the validation and
 * contract rules can be unit-tested on the JVM. The thin Bundle/Intent
 * glue lives in PersonIntent.kt.
 */
data class Person(
    val firstName: String,
    val lastName: String,
    val age: Int,
) {
    val displayName: String
        get() = "$firstName $lastName"
}

/** Result of parsing the three form fields. Only [Valid] carries a [Person]. */
sealed interface PersonParse {
    data class Valid(val person: Person) : PersonParse
    data class Invalid(val reason: String) : PersonParse
}

/** The oldest and youngest ages the form accepts. The original had no range. */
const val MIN_AGE = 0
const val MAX_AGE = 150

/**
 * Parses the raw form fields. Names are trimmed; an age must be a whole
 * number in range. Unlike the original, this never throws on bad input:
 * [String.toIntOrNull] returns null for non-numeric text and for overflow,
 * and both cases get the same clear message.
 */
fun parsePerson(firstNameText: String, lastNameText: String, ageText: String): PersonParse {
    val firstName = firstNameText.trim()
    if (firstName.isEmpty()) {
        return PersonParse.Invalid("Enter a first name.")
    }
    val lastName = lastNameText.trim()
    if (lastName.isEmpty()) {
        return PersonParse.Invalid("Enter a last name.")
    }
    val ageRaw = ageText.trim()
    if (ageRaw.isEmpty()) {
        return PersonParse.Invalid("Enter an age.")
    }
    val age = ageRaw.toIntOrNull()
    if (age == null || age < MIN_AGE || age > MAX_AGE) {
        return PersonParse.Invalid("Enter a whole-number age from $MIN_AGE to $MAX_AGE.")
    }
    return PersonParse.Valid(Person(firstName, lastName, age))
}

/**
 * Builds a [Person] from already-typed values, as received from intent
 * extras. Returns null when any value is missing or out of range, instead
 * of the original's silent `getInt` default of zero.
 */
fun personFromValues(firstName: String?, lastName: String?, age: Int?): Person? {
    val first = firstName?.trim().orEmpty()
    val last = lastName?.trim().orEmpty()
    if (first.isEmpty() || last.isEmpty()) return null
    if (age == null || age < MIN_AGE || age > MAX_AGE) return null
    return Person(first, last, age)
}

/** Action and extra names for the implicit-intent contract. */
object PersonContract {
    const val ACTION_ADD_PERSON = "com.enonymous1.implicitintentdemo.ACTION_ADD_PERSON"
    const val ACTION_VIEW_PERSON = "com.enonymous1.implicitintentdemo.ACTION_VIEW_PERSON"
    const val EXTRA_FIRST_NAME = "com.enonymous1.implicitintentdemo.EXTRA_STRING_FIRST_NAME"
    const val EXTRA_LAST_NAME = "com.enonymous1.implicitintentdemo.EXTRA_STRING_LAST_NAME"
    const val EXTRA_AGE = "com.enonymous1.implicitintentdemo.EXTRA_INT_AGE"

    // The original Full Sail contract, kept so the CE08 host that launched
    // the v1.0 app can still reach this screen. New callers use the names
    // above; both sets are accepted on the way in, and results always
    // carry the current names (see PersonIntent).
    const val LEGACY_ACTION_ADD_PERSON = "com.fullsail.android.jav2ce08.ACTION_ADD_PERSON"
    const val LEGACY_ACTION_VIEW_PERSON = "com.fullsail.android.jav2ce08.ACTION_VIEW_PERSON"
    const val LEGACY_EXTRA_FIRST_NAME = "com.fullsail.android.jav2ce08.EXTRA_STRING_FIRST_NAME"
    const val LEGACY_EXTRA_LAST_NAME = "com.fullsail.android.jav2ce08.EXTRA_STRING_LAST_NAME"
    const val LEGACY_EXTRA_AGE = "com.fullsail.android.jav2ce08.EXTRA_INT_AGE"

    fun isAddAction(action: String?): Boolean =
        action == ACTION_ADD_PERSON || action == LEGACY_ACTION_ADD_PERSON

    fun isViewAction(action: String?): Boolean =
        action == ACTION_VIEW_PERSON || action == LEGACY_ACTION_VIEW_PERSON
}
