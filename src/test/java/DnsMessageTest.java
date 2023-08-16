import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import se.helgestenstrom.DnsMessage;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DnsMessageTest {

    @Test
    void foo() {
        assertEquals(3, 1+2);
    }


    @Test
    void sampleMessageString() {

        // Setup
        var dnsMessage = new DnsMessage(22);

        // Exercise
        var message = dnsMessage.message();

        // Verify
        String messageExampleFromExercise = "00160100000100000000000003646e7306676f6f676c6503636f6d0000010001";
        assertEquals(messageExampleFromExercise, message);
    }

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
        var dnsMessage = new DnsMessage(id);

        // Exercise
        var message = dnsMessage.message();

        // Verify
        assertEquals(expected, message);
    }
}
