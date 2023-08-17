import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.helgestenstrom.DnsMessage;
import se.helgestenstrom.Id;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DnsMessageTest {

    public static Stream<Arguments> valuesForTest() {
        return Stream.of(
                Arguments.of(22, "00160100000100000000000003646e7306676f6f676c6503636f6d0000010001"),
                Arguments.of(23, "00170100000100000000000003646e7306676f6f676c6503636f6d0000010001")
        );
    }

    @ParameterizedTest
    @MethodSource("valuesForTest")
    void messageStringWithId(int id, String expected) {

        // Setup
        var dnsMessage = new DnsMessage(new Id(id));

        // Exercise
        var message = dnsMessage.message();

        // Verify
        assertEquals(expected, message);
    }
}
