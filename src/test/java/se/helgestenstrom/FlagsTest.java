package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FlagsTest {

    @Test
    void recursionDesired() {
        var flags = new Flags(true);

        assertEquals("0100", flags.hex());
    }
    @Test
    void recursionNotDesired() {
        var flags = new Flags(false);

        assertEquals("0000", flags.hex());
    }
}
