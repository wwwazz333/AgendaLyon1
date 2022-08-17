package com.iutcalendar.calendrier

import com.iutcalendar.calendrier.DateCalendrier.Companion.fillWithZeroAfter
import com.iutcalendar.calendrier.DateCalendrier.Companion.fillWithZeroBefore
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DateCalendrierTest {
    @Before
    fun before() {
        date = DateCalendrier(7, 3, 2001, 14, 30)
    }

    @Test
    fun testFillWithZeroBefore() {
        Assert.assertEquals(fillWithZeroBefore(4), "04")
        Assert.assertEquals(fillWithZeroBefore(40), "40")
    }

    @Test
    fun testFillWithZeroAfter() {
        Assert.assertEquals(fillWithZeroAfter(4), "40")
        Assert.assertEquals(fillWithZeroAfter(40), "40")
    }

    @Test
    fun testGetDay() {
        Assert.assertEquals(date!!.day.toLong(), 7)
    }

    @Test
    fun testSetDay() {
        date!!.day = 14
        Assert.assertEquals(date!!.day.toLong(), 14)
    }

    @Test
    fun testGetMonth() {
        Assert.assertEquals(date!!.month.toLong(), 3)
    }

    @Test
    fun testSetMonth() {
        date!!.month = 5
        Assert.assertEquals(date!!.month.toLong(), 5)
    }

    @Test
    fun testGetYear() {
        Assert.assertEquals(date!!.year.toLong(), 2001)
    }

    @Test
    fun testSetYear() {
        date!!.year = 2003
        Assert.assertEquals(date!!.year.toLong(), 2003)
    }

    @Test
    fun testGetHour() {
        Assert.assertEquals(date!!.hour.toLong(), 14)
    }

    @Test
    fun testSetHour() {
        date!!.hour = 23
        Assert.assertEquals(date!!.hour.toLong(), 23)
    }

    @Test
    fun testGetMinute() {
        Assert.assertEquals(date!!.minute.toLong(), 30)
    }

    @Test
    fun testSetMinute() {
        date!!.minute = 4
        Assert.assertEquals(date!!.minute.toLong(), 4)
    }

    @Test
    fun testSubTime() {
        Assert.assertEquals(
            date!!.subTime(DateCalendrier(0, 0, 0, 1, 30)),
            DateCalendrier(7, 3, 2001, 13, 0)
        )
    }

    @Test
    fun testAddTime() {
        Assert.assertEquals(
            date!!.addTime(DateCalendrier(0, 0, 0, 1, 31)),
            DateCalendrier(7, 3, 2001, 16, 1)
        )
    }

    @Test
    fun testSameDay() {
        Assert.assertTrue(date!!.sameDay(DateCalendrier(7, 3, 2001, 0, 0)))
        Assert.assertFalse(date!!.sameDay(DateCalendrier(7, 3, 2002, 0, 0)))
        Assert.assertFalse(date!!.sameDay(DateCalendrier(7, 4, 2001, 0, 0)))
        Assert.assertFalse(date!!.sameDay(DateCalendrier(1, 3, 2001, 0, 0)))
        Assert.assertFalse(date!!.sameDay(DateCalendrier(0, 0, 0, 0, 0)))
    }

    @Test
    fun testEquals() {
        Assert.assertEquals(date, DateCalendrier(7, 3, 2001, 14, 30))
        Assert.assertEquals(
            DateCalendrier(7, 3, 2001, 16, 1),
            DateCalendrier(7, 3, 2001, 16, 1)
        )
        Assert.assertNotEquals(date, DateCalendrier(7, 3, 2001, 23, 30))
        Assert.assertNotEquals(date, DateCalendrier(7, 5, 2001, 14, 30))
        Assert.assertNotEquals(date, DateCalendrier(1, 3, 2001, 23, 30))
    }

    companion object {
        private var date: DateCalendrier? = null
    }
}