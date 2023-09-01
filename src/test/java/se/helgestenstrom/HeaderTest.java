package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderTest {

    @Test
    void hexFromIdAndFlags() {
        Id id = new Id(0xabcd);
        Flags flags = new Flags(true);

        Header header = new Header(id, flags, 1, 20, 21, 22);
        String hex = header.asList().hex();

        assertEquals("abcd0100", hex.substring(0,8));
    }




}