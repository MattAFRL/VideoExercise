package au.com.afewroosloose.videoexercise.presentation.util

import java.util.concurrent.atomic.AtomicBoolean

class Event<T>(private val value: T) {
    private val expired = AtomicBoolean(false)

    fun getValue(): T? {
        return if (!expired.getAndSet(true)) {
            value
        } else null
    }
}
