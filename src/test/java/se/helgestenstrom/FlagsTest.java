package se.helgestenstrom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class FlagsTest {

    @Test
    void recursionDesired() {
        var flags = new Flags(true);

        assertEquals("0100", flags.asList().hex());
    }
    @Test
    void recursionNotDesired() {
        var flags = new Flags(false);

        assertEquals("0000", flags.asList().hex());
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
        assertEquals(hex, f.asList().hex());
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
        assertEquals(hex, f.asList().hex());
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
        assertEquals(hex, f.asList().hex());
    }


    @Test
    void isQueryFromInt() {
        // QR is the MSBit
        int responseNotQuery = 0x8000;
        int queryNotResponse = 0x0;
        Flags response = new Flags(responseNotQuery);
        Flags query = new Flags(queryNotResponse);
        assertAll(
                () -> assertFalse(response.isQuery()),
                () -> assertTrue(response.isResponse()),
                () -> assertTrue(query.isQuery()),
                () -> assertFalse(query.isResponse())
        );

//        assertFalse(new Flags(responseNotQuery).isQuery());
//        assertTrue(new Flags(responseNotQuery).isResponse());
//
//        assertTrue(new Flags(queryNotResponse).isQuery());
//        assertFalse(new Flags(queryNotResponse).isResponse());
    }

    @Test
    void opcode() {
        assertEquals(0xf, new Flags(0xffff).getOpcode());
        assertEquals(0, new Flags(0).getOpcode());
        assertEquals(5, new Flags( 5 << 11).getOpcode());
        assertEquals(5, new Flags( 0b0010100000000000).getOpcode());
    }

    @Test
    void authoritative() {
        assertFalse(new Flags(0).isAuthoritative());
        assertTrue(new Flags(1 << 10).isAuthoritative());
    }

    @Test
    void truncation() {
        assertFalse(new Flags(0).isTruncated());
        assertTrue(new Flags(1 << 9).isTruncated());
    }

    @Test
    void isRecursionDesired() {
        assertFalse(new Flags(0).isRecursionDesired());
        assertTrue(new Flags(1 << 8).isRecursionDesired());
    }

    @Test
    void isRecursionAvailable() {
        assertFalse(new Flags(0).isRecursionAvailable());
        assertTrue(new Flags(1 << 7).isRecursionAvailable());
    }

    @Test
    void rCode() {
        assertEquals(RCode.Refused, new Flags(5).getRCode());
    }

}
