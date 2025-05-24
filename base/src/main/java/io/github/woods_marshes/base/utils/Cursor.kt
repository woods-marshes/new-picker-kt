package io.github.woods_marshes.base.utils

import android.database.Cursor

inline fun Cursor.forEach(block: Cursor.() -> Unit) {
    if (moveToFirst()) {
        do {
            block()
        } while (moveToNext())
    }
}

inline fun Cursor.forEachIndexed(block: Cursor.(index: Int) -> Unit) {
    if (moveToFirst()) {
        var index = 0
        do {
            block(index)
            index++
            } while (moveToNext())
    }
}

inline fun <T> Cursor.map(block: (Cursor) -> T): List<T> {
    val result = mutableListOf<T>()

    forEach {
        result.add(block(this))
    }
    return result
}

inline fun <T> Cursor.pagedMap(offset: Int, limit: Int, block: (Cursor) -> T): List<T> {
    var i = 0
    val result = mutableListOf<T>()
    while (moveToPosition(offset + i)) {
        result.add(block(this))

        i++
        if (i >= limit) break
    }
    return result
}