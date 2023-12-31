package se.helgestenstrom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DnsMessageTest {

    @SuppressWarnings("unused")
    private final String exampleMessage = "00168080000100020000000003646e7306676f6f676c6503636f6d0000010001c00c0001000100000214000408080808c00c0001000100000214000408080404";

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
        String host = "dns.google.com";
        final Question question = new Question(new Name(host), 1, 1);
        var dnsMessage = new DnsMessage(new Header(id, flags, 1, 0, 0, 0), List.of(question), List.of(), List.of(), List.of());

        // Exercise
        var message = dnsMessage.byteList().hex();

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
        String host = "dns.google.com";
        final Question question = new Question(new Name(host), 1, 1);
        var dnsMessage = new DnsMessage(new Header(id, flags, 1, 0, 0, 0), List.of(question), List.of(), List.of(), List.of());

        // Exercise
        var message = dnsMessage.byteList().hex();

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
        final Question question = new Question(new Name(host), 1, 1);
        var dnsMessage = new DnsMessage(new Header(id, flags, 1, 0, 0, 0), List.of(question), List.of(), List.of(), List.of());

        // Exercise
        var message = dnsMessage.byteList().hex();

        // Verify
        assertEquals(expected, message);
    }


    @Test
    void byteArray() {
        // Setup
        Id id = new Id(22);
        final Flags flags = new Flags(true);
        String host = "examplehost";
        final Question abc = new Question(new Name(host), 1, 1);
        DnsMessage dnsMessage = new DnsMessage(new Header(id, flags, 1, 20, 21, 22), List.of(abc), List.of(), List.of(), List.of());

        // Exercise
        byte[] bytes = dnsMessage.bytes();

        // Verify a few values in the byte array
        assertEquals(0, bytes[0]);
        assertEquals(22, bytes[1]);
        assertEquals(1, bytes[bytes.length - 1]);

    }


}
