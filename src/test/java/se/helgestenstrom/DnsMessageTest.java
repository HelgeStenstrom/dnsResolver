package se.helgestenstrom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HexFormat;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DnsMessageTest {

    public static Stream<Arguments> varyId() {
        return Stream.of(
                Arguments.of(22, "00160100000100000000000003646e7306676f6f676c6503636f6d0000010001"),
                Arguments.of(23, "00170100000100000000000003646e7306676f6f676c6503636f6d0000010001")
        );
    }

    @ParameterizedTest
    @MethodSource("varyId")
    void messageStringWithId(int idNo, String expected) {

        // Setup
        Id id = new Id(idNo);
        Flags flags = new Flags(true);
        var dnsMessage = new DnsMessage(new Header(id, flags), new Question("dns.google.com", "0001", "0001"));

        // Exercise
        var message = dnsMessage.hex();

        // Verify
        assertEquals(expected, message);
    }

    public static Stream<Arguments> varyDesiredRecursion() {
        return Stream.of(
                Arguments.of(true, "00160100000100000000000003646e7306676f6f676c6503636f6d0000010001")
                , Arguments.of(false, "00160000000100000000000003646e7306676f6f676c6503636f6d0000010001")
        );
    }

    @ParameterizedTest
    @MethodSource("varyDesiredRecursion")
    void messageStringWithDesiredRecursion(boolean desiredRecursion, String expected) {

        // Setup
        Id id = new Id(22);
        Flags flags = new Flags(desiredRecursion);
        var dnsMessage = new DnsMessage(new Header(id, flags), new Question("dns.google.com", "0001", "0001"));

        // Exercise
        var message = dnsMessage.hex();

        // Verify
        assertEquals(expected, message);
    }

    public static Stream<Arguments> varyHost() {
        return Stream.of(
                Arguments.of("dns.google.com", "00160100000100000000000003646e7306676f6f676c6503636f6d0000010001")
                , Arguments.of("abc", "001601000001000000000000036162630000010001")
        );
    }

    @ParameterizedTest
    @MethodSource("varyHost")
    void messageforHost(String host, String expected) {

        // Setup
        Id id = new Id(22);
        Flags flags = new Flags(true);
        var dnsMessage = new DnsMessage(new Header(id, flags), new Question(host, "0001", "0001"));

        // Exercise
        var message = dnsMessage.hex();

        // Verify
        assertEquals(expected, message);
    }

    @Test
    void byteArray() {
        // Setup
        Id id = new Id(22);
        final Flags flags = new Flags(true);
        DnsMessage dnsMessage = new DnsMessage(new Header(id, flags), new Question("abc", "0001", "0001"));

        // Exercise
        byte[] bytes = dnsMessage.bytes();

        // Verify a few values in the byte array
        assertEquals(0, bytes[0]);
        assertEquals(22, bytes[1]);
        assertEquals(1, bytes[bytes.length - 1]);

    }

    @Test
    void idFromString() {
        String encodedMessage = "10020100000100000000000003646e7306676f6f676c6503636f6d0000010001";
        DnsMessage dm = DnsMessage.from(encodedMessage);
        assertEquals(0x1002, dm.id());
        assertEquals(0x1002, dm.getHeader().getId().id());
    }

    @Test
    void idFromByteArray() {
        String encodedMessage = "10030100000100000000000003646e7306676f6f676c6503636f6d0000010001";
        byte[] bytes = HexFormat.of().parseHex(encodedMessage);
        // 0x1003 = dec 4099.
        DnsMessage dm = DnsMessage.from(bytes);
        assertEquals(0x1003, dm.id());

    }

    @Test
    void flagFromString() {
        String encodedMessage = "10020100000100000000000003646e7306676f6f676c6503636f6d0000010001";
        DnsMessage dm = DnsMessage.from(encodedMessage);
        Header header = dm.getHeader();
        Flags flags = header.getFlags();
        assertNotNull(flags);

        assertEquals(0x1002, header.getId().id());
    }

    @Test
    //@Disabled("Test not done yet")
    void decodeExampleString() {
        var example = "00168080000100020000000003646e7306676f6f676c6503636f6d0000010001c00c0001000100000214000408080808c00c0001000100000214000408080404";

        DnsMessage message = DnsMessage.from(example);

        int id = message.id();
        assertEquals(22, id);

        Header header = message.getHeader();

        Id id1 = header.getId();
        assertEquals(22, id1.id());

        Flags flags = header.getFlags();

        String hex = flags.hex();
        assertEquals("8080", hex);

    }

}
