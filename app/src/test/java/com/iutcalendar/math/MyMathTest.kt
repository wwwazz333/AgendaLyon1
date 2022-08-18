package com.iutcalendar.math

import com.iutcalendar.math.MyMath.Companion.roundAt
import org.junit.Assert
import org.junit.Test

class MyMathTest {
    @Test
    fun testRoundAt() {
        Assert.assertEquals(20, roundAt(22, 5))
        Assert.assertEquals(25, roundAt(23, 5))
        Assert.assertEquals(35, roundAt(35, 5))
        Assert.assertEquals(40, roundAt(39, 10))
        Assert.assertEquals(20, roundAt(20, 10))
        Assert.assertEquals(10, roundAt(11, 10))
        Assert.assertEquals(20, roundAt(15, 10))
        Assert.assertEquals(0, roundAt(4, 10))
    }
}
