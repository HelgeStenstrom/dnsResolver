package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    void fourChars() {
        var toShort = "abc";
        var rightLength = "abcd";
        var toLong = "abcde";

        assertThrows(IllegalArgumentException.class, () -> TwoBytes.of(toShort));
        assertThrows(IllegalArgumentException.class, () -> TwoBytes.of(toLong));

        // Doesn't throw
        TwoBytes.of(rightLength);
    }

}