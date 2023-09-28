package se.helgestenstrom;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameTest {

    @Test
    void hex() {

        String raw = "dns.google.com";

        var e = new Name(raw);

        String h = e.asList().hex();

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
    void hexFromRaw(String raw, String encoded) {

        var e = new Name(raw);

        assertEquals(encoded, e.asList().hex());
    }

    @Test
    void labels() {

        var raw = "abc.def";
        var name = new Name(raw);

        List<String> expected = List.of("abc", "def");
        assertEquals(expected, name.getLabels());
    }






    /*
    * Names can be compressed.
    * Can compressed names occur anywhere in the DNS message?
    * Does a function that returns a decoded name need both the whole message
    * (as hex string or byte list) and a starting index?
    * Does the decoding function need to differentiate different forms of names,
    * like
    * - a sequence of labels ending in a zero octet
    * - a pointer
    * - a sequence of labels ending with a pointer
    *
    * The RFC 1035 talks about labels. They seem to be the words between the dots,
    * like in dns.google.com, we have 3 labels: dns, google and com.
    * Each label also has a length prefix. If it's 0, then the name ends.
    * So the label sequence ends with a zero.
    *
    * a.b.c can be coded like:
    * 1, 'a', 1, 'b', 1, 'c', 0
    * where 'a', 'b' and 'c' are represented by their ascii codes.
    *
    * foo.a.b.c can be represented by
    * 3, 'foo', P
    * where P is a pointer that points to the first sequence (1, 'a', 1, 'b', 1, 'c', 0)
    *
    * A pointer is a 16-bit word where the 2 most significant bits are 11, and the rest is an offset
    * from the start of the DNS message (as a list of octets, (list of bytes or integers)).
    *
    * If a.b.c is needed again, it can simply be represented by P (as given above).
    *
    * The message consists of a stream (list) of bytes, but often 2-octet words are considered.
    * In a name, if the first octet (normally the label length byte) has its 2 most significant bits set,
    * then this byte and the following forms a 16-bit word which is a pointer.
    * */


}
