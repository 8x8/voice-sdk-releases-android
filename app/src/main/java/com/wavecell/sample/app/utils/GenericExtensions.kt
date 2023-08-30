package com.wavecell.sample.app.utils

fun <T> T?.notNull(f: (T) -> Unit) {
    if (this != null) {
        f(this)
    }
}

fun <T> T?.isNotNull() = this != null
fun <T> T?.isNull() = this == null

inline fun <T : Any> T?.ifNull(defaultValue: () -> T): T {
    return this ?: defaultValue()
}

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T {
    return if (condition) apply(block) else this
}

inline fun <T> T.applyIf(condition: () -> Boolean, block: T.() -> Unit): T {
    return applyIf(condition(), block)
}

inline fun <T1 : Any, T2 : Any, R : Any> manyLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> manyLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> manyLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, block: (T1, T2, T3, T4) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, R : Any> manyLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, p5: T5?, block: (T1, T2, T3, T4, T5) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null) block(p1, p2, p3, p4, p5) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, R : Any> manyLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, p5: T5?, p6: T6?, block: (T1, T2, T3, T4, T5, T6) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null && p6 != null) block(p1, p2, p3, p4, p5, p6) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, R : Any> manyLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, p5: T5?, p6: T6?, p7: T7?, block: (T1, T2, T3, T4, T5, T6, T7) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null && p6 != null && p7 != null) block(p1, p2, p3, p4, p5, p6, p7) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, R : Any> manyLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, p5: T5?, p6: T6?, p7: T7?, p8: T8?, block: (T1, T2, T3, T4, T5, T6, T7, T8) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null && p6 != null && p7 != null && p8 != null) block(p1, p2, p3, p4, p5, p6, p7, p8) else null
}