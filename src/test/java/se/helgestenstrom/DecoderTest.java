package se.helgestenstrom;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DecoderTest {

    @Test
    @DisplayName("ID from message")
    void idFromEncodedMessage() {

        // Setup
        ByteList encodedWithId = encodeHeader(0x1234);

        Decoder decoder = new Decoder(encodedWithId);

        // Exercise
        int id = decoder.getId();

        // Verify
        assertEquals(0x1234, id);
    }
    @Test
    @DisplayName("Header with ID from message")
    void idViaHeaderFromEncodedMessage() {

        // Setup
        ByteList encodedWithId = encodeHeader(0x12ab);

        Decoder decoder = new Decoder(encodedWithId);

        // Exercise
        Header header = decoder.getHeader();

        // Verify
        assertEquals(0x12ab, header.getId().id());
    }

    private static ByteList encodeHeader(int id) {
        List<Integer> idPart = ByteList.fromInt(id);
        List<Integer> bytes = List.of(
                0x0, 0x0,
                0x0, 0x0,
                0x0, 0x0,
                0x0, 0x0,
                0x0, 0x0
        );
        ByteList encodedWithId = new ByteList();
        encodedWithId.addAll( idPart);
        encodedWithId.addAll( bytes);
        return encodedWithId;
    }


}
