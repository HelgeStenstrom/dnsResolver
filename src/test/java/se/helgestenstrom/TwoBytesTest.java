package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TwoBytesTest {

    @Test
    void fromStringToHex() {
        assertEquals("10ab", TwoBytes.of("10ab").hex());
        assertEquals("00ab", TwoBytes.of("00ab").hex());
    }

    @Test
    void stringToInt() {
        assertEquals(1025, TwoBytes.of("0401").asInt());
        assertEquals(16, TwoBytes.of("0010").asInt());
    }


}