package com.iutcalendar.math

class MyMath {
    companion object {
        fun roundAt(x: Int, step: Int = 5): Int {
            Int
            return (x % step).let { if (it < step / 2.0) x - (it) else x + (step - it) }
        }

        fun <T : Comparable<T>> between(x: T, min: T, max: T): T {
            return if (x in min..max) x
            else if (x < min) min
            else max
        }
    }
}