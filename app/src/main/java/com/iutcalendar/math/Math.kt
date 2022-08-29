package com.iutcalendar.math

class MyMath {
    companion object {
        fun roundAt(x: Int, step: Int = 5): Int {
            return (x % step).let { if (it < step / 2.0) x - (it) else x + (step - it) }
        }
    }
}