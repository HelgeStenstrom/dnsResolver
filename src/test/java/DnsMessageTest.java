import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.helgestenstrom.DnsMessage;
import se.helgestenstrom.Flags;
import se.helgestenstrom.Id;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var dnsMessage = new DnsMessage(id, flags);

        // Exercise
        var message = dnsMessage.message();

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
        var dnsMessage = new DnsMessage(id, flags);

        // Exercise
        var message = dnsMessage.message();

        // Verify
        assertEquals(expected, message);
    }
}
