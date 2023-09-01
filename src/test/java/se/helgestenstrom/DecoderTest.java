package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class DecoderTest {

    @Test
    @DisplayName("ID from message")
    void idFromEncodedMessage() {

        // Setup
        ByteList encodedWithId = encodeHeader(0x1234, 0, 0, 0, 0, 0);

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
        ByteList encodedWithId = encodeHeader(0x12ab, 0, 0, 0, 0, 0);

        Decoder decoder = new Decoder(encodedWithId);

        // Exercise
        Header header = decoder.getHeader();

        // Verify
        assertEquals(0x12ab, header.getId().id());
    }

    @ParameterizedTest
    @CsvSource({
            "true, 0x8000",
            "false,  0",
    })
    @DisplayName("Flags via the header")
    void flagsViaHeader(boolean isResponse, int flags) {

        // Setup
        ByteList encodedWithFlags = encodeHeader(0, flags, 0, 0, 0, 0);
        Decoder decoder = new Decoder(encodedWithFlags);

        // Exercise
        Header header = decoder.getHeader();

        // Verify
        assertEquals(isResponse, header.getFlags().isResponse());
    }

    @Test
    void countsViaTheHeader() {

        // Setup
        ByteList encodedWithFlags = encodeHeader(0, 0, 0x1001, 0x1002, 0x1003, 0x1004);
        Decoder decoder = new Decoder(encodedWithFlags);

        // Exercise
        Header header = decoder.getHeader();

        // Verify
        assertAll(
                () -> assertEquals(0x1001, header.getQdCount()),
                () -> assertEquals(0x1002, header.getAnCount()),
                () -> assertEquals(0x1003, header.getNsCount()),
                () -> assertEquals(0x1004, header.getArCount())
        );
    }

    @Test
    void oneQuestionSingleCharacterName() {

        // Setup
        int questionCount = 1;
        ByteList header = encodeHeader(0, 0, questionCount, 0, 0, 0);

        ByteList encodedName = new ByteList(List.of(1, (int) 'a', 0));
        ByteList qType = new ByteList(List.of(0xab, 0xcd));
        ByteList qClass = new ByteList(List.of(0x34, 0x56));
        ByteList message = new ByteList()
                .append(header, encodedName, qType, qClass);

        Decoder decoder = new Decoder(message);

        // Exercise
        List<Question> questions = decoder.getQuestions();


        // Verify
        assertEquals(1, questions.size());
        Question question = questions.get(0);
        assertEquals(List.of("a"), question.getLabels());


    }

    public static Stream<Arguments> varyName() {
        return Stream.of(
                Arguments.of(List.of(1, (int) 'a', 0), List.of("a"))
                , Arguments.of(List.of(1, (int) 'b', 0), List.of("b"))
                , Arguments.of(List.of(3, (int) 'a', (int) 'b', (int) 'c', 0), List.of("abc"))
                , Arguments.of(List.of(
                        1, (int) 'a',
                        3, (int) 'b', (int) 'c', (int) 'd',
                        0), List.of("a", "bcd"))
        );
    }

    @ParameterizedTest
    @MethodSource("varyName")
    void oneQuestionSomeNames(List<Integer> bytes, List<String> expected) {

        // Setup
        int questionCount = 1;
        ByteList header = encodeHeader(0, 0, questionCount, 0, 0, 0);

        ByteList encodedName = new ByteList(bytes);
        ByteList qType = new ByteList(List.of(0xab, 0xcd));
        ByteList qClass = new ByteList(List.of(0x34, 0x56));
        ByteList message = new ByteList()
                .append(header, encodedName, qType, qClass);

        Decoder decoder = new Decoder(message);

        // Exercise
        List<Question> questions = decoder.getQuestions();


        // Verify
        assertEquals(1, questions.size());
        Question question = questions.get(0);
        assertEquals(expected, question.getLabels());


    }

    private static ByteList encodeHeader(int id, int flags, int qdCount, int anCount, int nsCount, int arCount) {
        List<Integer> idPart = ByteList.fromInt(id);
        ByteList flagsPart = ByteList.fromInt(flags);
        ByteList qdCount1 = ByteList.fromInt(qdCount);
        ByteList anCount1 = ByteList.fromInt(anCount);
        ByteList nsCount1 = ByteList.fromInt(nsCount);
        ByteList arCount1 = ByteList.fromInt(arCount);


        ByteList encodedWithId = new ByteList();
        encodedWithId.addAll(idPart);
        encodedWithId.addAll(flagsPart);
        encodedWithId.addAll(qdCount1);
        encodedWithId.addAll(anCount1);
        encodedWithId.addAll(nsCount1);
        encodedWithId.addAll(arCount1);
        return encodedWithId;
    }


}
