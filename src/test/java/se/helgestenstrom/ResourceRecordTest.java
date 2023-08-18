package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ResourceRecordTest {

    @Test
    @Disabled("Test not done")
    void foo() {

        ResourceRecord resourceRecord = new ResourceRecord();
        String hex = resourceRecord.hex();
        assertEquals("foobar", hex);

    }

}