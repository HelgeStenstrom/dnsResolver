package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionTest {

    @Test
    void paramsFromQuestion() {
        Name qName = new Name("qname");
        int qType = 0x1234;
        int qClass = 0xabcd;

        Question question = new Question(qName, qType, qClass);

        assertEquals(qType, question.getType());
        assertEquals(qClass, question.getQClass());
    }

    @Test
    void nameFromQuestion() {
        Name qName = new Name("qname");


        Question question = new Question(qName, 0, 0);

        assertEquals(qName, question.getName());
    }

    @Test
    void asString() {
        Name qName = new Name("qname");

        Question question = new Question(qName, 1, 0);

        assertTrue(question.toString().contains("a host address")); // Corresponds to type 1.
    }
}