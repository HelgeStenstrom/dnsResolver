package se.helgestenstrom;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class ResourceRecordTest {

    @Test
    @Disabled("Must learn about name compression before this is implemented.")
    void foo() {

        String hexOfAnswers = "c00c0001000100000214000408080808c00c0001000100000214000408080404";

        ResourceRecord resourceRecord = ResourceRecord.of(hexOfAnswers);


    }

}