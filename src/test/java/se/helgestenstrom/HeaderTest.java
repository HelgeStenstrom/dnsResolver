package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HeaderTest {

    @Test
    void hexFromIdAndFlags() {
        Id id = new Id(0xabcd);
        Flags flags = new Flags(true);

        Header header = new Header(id, flags, 1, 20, 21, 22);
        String hex = header.hex();

        assertEquals("abcd0100", hex.substring(0,8));
    }

    @Test
    void fromHexString() {
        Header header = Header.fromHex("0016abcd1002200330044005");

        assertAll(
                () -> assertEquals(0x16, header.getId().id()),
                () -> assertEquals("abcd", header.getFlags().hex()),
                () -> assertEquals(0x1002, header.getQdCount()),
                () -> assertEquals(0x2003, header.getAnCount()),
                () -> assertEquals(0x3004, header.getNsCount()),
                () -> assertEquals(0x4005, header.getArCount())
        );
    }

    @Test
    void idFromHexString() {
        Header header = Header.fromHex("0016abcd1002200330044005");
        assertEquals(0x16, header.getId().id());
    }

    @Test
    void flagsFromHexString() {
        Header header = Header.fromHex("0016abcd1002200330044005");
        assertEquals("abcd", header.getFlags().hex());
    }

    @Test
    void qdCountFromHexString() {
        Header header = Header.fromHex("0016abcd1002200330044005");
        assertEquals(0x1002, header.getQdCount());
    }

    @Test
    void anCountFromHexString() {
        Header header = Header.fromHex("0016abcd1002200330044005");
        assertEquals(0x2003, header.getAnCount());
    }

    @Test
    void nsCountFromHexString() {
        Header header = Header.fromHex("0016abcd1002200330044005");
        assertEquals(0x3004, header.getNsCount());
    }

    @Test
    void arCountFromHexString() {
        Header header = Header.fromHex("0016abcd1002200330044005");
        assertEquals(0x4005, header.getArCount());
    }

}