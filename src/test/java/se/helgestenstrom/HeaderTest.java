package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HeaderTest {

    @Test
    void hexFromIdAndFlags() {
        Id id = new Id(0xabcd);
        Flags flags = new Flags(true);

        Header header = new Header(id, flags);
        String hex = header.hex();

        assertEquals("abcd0100", hex.substring(0,8));
    }

}