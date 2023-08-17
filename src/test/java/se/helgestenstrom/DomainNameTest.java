package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class DomainNameTest {

    @Test
    void hex() {

        String raw = "dns.google.com";

        var e = new DomainName(raw);

        String h = e.hex();

        assertEquals("03646e7306676f6f676c6503636f6d00", h);
    }

    public static Stream<Arguments> varyRaw() {
        return Stream.of(
                Arguments.of("dns.google.com", "03646e7306676f6f676c6503636f6d00")
                , Arguments.of("dns", "03646e7300")
                , Arguments.of("a", "016100")
        );
    }

    @ParameterizedTest
    @MethodSource("varyRaw")
    void tst(String raw, String encoded) {

        var e = new DomainName(raw);

        assertEquals(encoded, e.hex());
    }




}
