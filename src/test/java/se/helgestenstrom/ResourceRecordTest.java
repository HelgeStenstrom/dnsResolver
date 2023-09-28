package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceRecordTest {

    @Test
    void testToString() {
        ResourceRecord resourceRecord = new ResourceRecord(new Name("foobar"), 1, 0, 123, new ByteList(List.of()));

        assertTrue(resourceRecord.toString().contains("a host address")); // corresponds to type 1.
    }
}