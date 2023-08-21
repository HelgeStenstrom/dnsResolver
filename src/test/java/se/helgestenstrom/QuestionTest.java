package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class QuestionTest {

    @Test
    void nameFromHex() {

        String hexQuestionAndFollowingData = "03646e7306676f6f676c6503636f6d0000010001c00c0001000100000214000408080808c00c0001000100000214000408080404";

        Question q = Question.of(hexQuestionAndFollowingData);

        DomainName name = q.getName();
        assertEquals("03646e7306676f6f676c6503636f6d00", name.hex());
        assertEquals("dns.google.com", name.getName());
    }

    @Test
    void typeAndStatusFromHex() {

        String hexQuestionAndFollowingData = "03646e7306676f6f676c6503636f6d0000030004c00c0001000100000214000408080808c00c0001000100000214000408080404";

        Question q = Question.of(hexQuestionAndFollowingData);

        assertEquals(3, q.getQType().asInt());
        assertEquals(4, q.getQClass().asInt());

    }

}