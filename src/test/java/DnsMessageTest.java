import org.junit.jupiter.api.Test;
import se.helgestenstrom.DnsMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DnsMessageTest {

    @Test
    void foo() {
        assertEquals(3, 1+2);
    }


    @Test
    void sampleMessageString() {

        // Setup
        var dnsMessage = new DnsMessage();

        // Exercise
        var message = dnsMessage.message();

        // Verify
        String messageExampleFromExercise = "00160100000100000000000003646e7306676f6f676c6503636f6d0000010001";
        assertEquals(messageExampleFromExercise, message);
    }
}
