package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ResourceRecordTest {

    /**
     * See <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.4"> Message compression</a>
     */
    @Test
    @Disabled("Must learn about name compression before this is implemented.")
    void foo() {

        String hexOfAnswers = "c00c0001000100000214000408080808c00c0001000100000214000408080404";

        /*
        * c00c.0001000100000214000408080808c00c0001000100000214000408080404
        * c00c - 1100 0000 0000 1100 - offset 12
        * 0001 -
        * 0001 -
        * 0000 -
        * 02 - 2 characters follow
        * 1400 - offset a lot
        * */

        String pointerHex = hexOfAnswers.substring(0, 4);


        ResourceRecord resourceRecord = ResourceRecord.of(hexOfAnswers);


        assertEquals(2, 1 + 3, "Test not done");
    }

}