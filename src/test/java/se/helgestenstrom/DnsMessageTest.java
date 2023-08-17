package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
        var dnsMessage = new DnsMessage(id, flags, "dns.google.com");

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
        var dnsMessage = new DnsMessage(id, flags, "dns.google.com");

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
        var dnsMessage = new DnsMessage(id, flags, host);

        // Exercise
        var message = dnsMessage.hex();

        // Verify
        assertEquals(expected, message);
    }

    @Test
    void byteArray() {
        // Setup
        Id id = new Id(22);
        DnsMessage dnsMessage = new DnsMessage(id, new Flags(true), "abc");

        // Exercise
        byte[] bytes = dnsMessage.bytes();

        // Verify a few values in the byte array
        assertEquals(0, bytes[0]);
        assertEquals(22, bytes[1]);
        assertEquals(1, bytes[bytes.length-1]);

    }
}
