package se.helgestenstrom;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecoderTest {

    @Test
    @DisplayName("Header with ID from message")
    void idFromEncodedMessage() {

        // Setup
        ByteList encodedWithId = new ByteList(List.of(
                0x12, 0x34,
                0x0, 0x0,
                0x0, 0x0,
                0x0, 0x0,
                0x0, 0x0,
                0x0, 0x0
        ));

        Decoder decoder = new Decoder(encodedWithId);

        // Exercise
        int id = decoder.getId();

        // Verify
        assertEquals(0x1234, id);

    }
}
