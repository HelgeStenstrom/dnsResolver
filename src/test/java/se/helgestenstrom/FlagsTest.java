package se.helgestenstrom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @ParameterizedTest
    @CsvSource({
            "false, '0000'",
            "true,  '0100'",
    })
    void recursionDesired(boolean desired, String hex) {
        Flags f = new Flags.Builder()
                .setRecursionDesired(desired)
                .build();
        assertEquals(hex, f.hex());
    }

    @ParameterizedTest
    @CsvSource({
            "false, '0100'",
            "true,  '8100'",
    })
    void queryResponse(boolean isResponse, String hex) {
        Flags f = new Flags.Builder()
                .setResponse(isResponse)
                .build();
        assertEquals(hex, f.hex());
    }

    @ParameterizedTest
    @CsvSource({
            "false, '0000'",
            "true,  '0400'",
    })
    void authoritative(boolean isAuthoritative, String hex) {
        Flags f = new Flags.Builder()
                .setIsAuthoritative(isAuthoritative)
                .setRecursionDesired(false)
                .build();
        assertEquals(hex, f.hex());
    }

    @Test
    void fromHex() {

        Flags f = new Flags("abcd");

        assertEquals("abcd", f.hex());
    }

}
