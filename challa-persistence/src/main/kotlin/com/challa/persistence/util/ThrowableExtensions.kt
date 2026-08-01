package com.challa.persistence.util

inline fun <reified T : Throwable> Throwable.findCause(): T? {
    var current: Throwable? = this

    while (current != null) {
        if (current is T) {
            return current
        }

        val next = current.cause
        if (next == current) {
            return null
        }

        current = next
    }

    return null
}
