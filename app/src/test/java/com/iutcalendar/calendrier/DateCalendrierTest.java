package com.iutcalendar.calendrier;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DateCalendrierTest {
    private static DateCalendrier date;

    @Before
    public void before() {
        date = new DateCalendrier(7, 3, 2001, 14, 30);
    }

    @Test
    public void testFillWithZeroBefore() {
        assertEquals(DateCalendrier.fillWithZeroBefore(4), "04");
        assertEquals(DateCalendrier.fillWithZeroBefore(40), "40");
    }

    @Test
    public void testFillWithZeroAfter() {
        assertEquals(DateCalendrier.fillWithZeroAfter(4), "40");
        assertEquals(DateCalendrier.fillWithZeroAfter(40), "40");
    }

    @Test
    public void testGetDay() {
        assertEquals(date.getDay(), 7);
    }

    @Test
    public void testSetDay() {
        date.setDay(14);
        assertEquals(date.getDay(), 14);
    }

    @Test
    public void testGetMonth() {
        assertEquals(date.getMonth(), 3);
    }

    @Test
    public void testSetMonth() {
        date.setMonth(5);
        assertEquals(date.getMonth(), 5);
    }

    @Test
    public void testGetYear() {
        assertEquals(date.getYear(), 2001);
    }

    @Test
    public void testSetYear() {
        date.setYear(2003);
        assertEquals(date.getYear(), 2003);
    }

    @Test
    public void testGetHour() {
        assertEquals(date.getHour(), 14);
    }

    @Test
    public void testSetHour() {
        date.setHour(23);
        assertEquals(date.getHour(), 23);
    }

    @Test
    public void testGetMinute() {
        assertEquals(date.getMinute(), 30);
    }

    @Test
    public void testSetMinute() {
        date.setMinute(4);
        assertEquals(date.getMinute(), 4);
    }

    @Test
    public void testSubTime() {
        assertEquals(date.subTime(new DateCalendrier(0, 0, 0, 1, 30)),
                new DateCalendrier(7, 3, 2001, 13, 0));
    }

    @Test
    public void testAddTime() {
        assertEquals(date.addTime(new DateCalendrier(0, 0, 0, 1, 31)),
                new DateCalendrier(7, 3, 2001, 16, 1));
    }

    @Test
    public void testSameDay() {
        assertTrue(date.sameDay(new DateCalendrier(7, 3, 2001, 0, 0)));
        assertFalse(date.sameDay(new DateCalendrier(7, 3, 2002, 0, 0)));
        assertFalse(date.sameDay(new DateCalendrier(7, 4, 2001, 0, 0)));
        assertFalse(date.sameDay(new DateCalendrier(1, 3, 2001, 0, 0)));
        assertFalse(date.sameDay(new DateCalendrier(0, 0, 0, 0, 0)));
    }

    @Test
    public void testEquals() {
        assertEquals(date, new DateCalendrier(7, 3, 2001, 14, 30));
        assertEquals(new DateCalendrier(7, 3, 2001, 16, 1),
                new DateCalendrier(7, 3, 2001, 16, 1));

        assertNotEquals(date, new DateCalendrier(7, 3, 2001, 23, 30));
        assertNotEquals(date, new DateCalendrier(7, 5, 2001, 14, 30));
        assertNotEquals(date, new DateCalendrier(1, 3, 2001, 23, 30));
    }
}