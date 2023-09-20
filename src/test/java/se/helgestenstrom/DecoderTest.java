package se.helgestenstrom;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        ByteList question1 = questionWithNameFromBytes(bytes, 0xabcd, 0x3456);
        ByteList message = header
                .append(question1);

        Decoder decoder = new Decoder(message);

        // Exercise

        ParseResult<List<Question>> listParseResult = decoder.getQuestions();
        List<Question> questions = listParseResult.getResult();


        // Verify
        assertEquals(1, questions.size());
        Question question = questions.get(0);
        assertEquals(expected, question.getLabels());
    }

    @Test
    @DisplayName("Type and class of Question")
    void oneQuestionTypeAndClass() {

        // Setup
        int questionCount = 1;
        ByteList header = encodeHeader(0, 0, questionCount, 0, 0, 0);

        int type = 1001;
        int qClass = 1002;
        ByteList question1 = questionWithNameFromBytes(List.of(1, (int) 'a', 0), type, qClass);
        ByteList message = header
                .append(question1);

        Decoder decoder = new Decoder(message);

        // Exercise

        ParseResult<List<Question>> listParseResult = decoder.getQuestions();
        List<Question> questions = listParseResult.getResult();


        // Verify
        assertEquals(1, questions.size());
        Question question = questions.get(0);
        assertEquals(type, question.getType());
        assertEquals(qClass, question.getQClass());
    }

    private static ByteList questionWithNameFromBytes(List<Integer> bytes, int qType, int qClass) {
        ByteList encodedName = new ByteList(bytes);
        ByteList qTypeBl = ByteList.fromInt(qType);
        ByteList qClassBl = ByteList.fromInt(qClass);
        return encodedName.append(qTypeBl, qClassBl);
    }

    public static Stream<Arguments> secondNames() {
        return Stream.of(
                Arguments.of(
                        List.of(
                                1, (int) 'a',
                                3, (int) 'b', (int) 'c', (int) 'd',
                                0),
                        List.of("a", "bcd"))
        );
    }


    @ParameterizedTest
    @MethodSource("secondNames")
    @DisplayName("Two equal questions, check second")
    void twoEqualQuestions(List<Integer> bytes, List<String> expected) {

        // Setup
        int questionCount = 2;
        ByteList header = encodeHeader(0, 0, questionCount, 0, 0, 0);

        ByteList question1 = questionWithNameFromBytes(bytes, 12, 34);
        ByteList question2 = questionWithNameFromBytes(bytes, 1002, 1003);
        ByteList message = header
                .append(question1, question2);

        Decoder decoder = new Decoder(message);

        // Exercise

        ParseResult<List<Question>> listParseResult = decoder.getQuestions();
        List<Question> questions = listParseResult.getResult();


        // Verify
        assertEquals(2, questions.size());
        Question q1 = questions.get(0);
        Question q2 = questions.get(1);
        assertEquals(expected, q2.getLabels());
        assertEquals(12, q1.getType());
        assertEquals(34, q1.getQClass());
        assertEquals(1002, q2.getType());
        assertEquals(1003, q2.getQClass());

    }

    /**
     * Verifies that the second question name is the same as that of the first question,
     * and that the type and class of the second question is what we expect.
     */
    @Test
    @DisplayName("Two Questions, name of second  is pointer")
    void twoQuestionsSecondPointer() {

        // Setup
        int questionCount = 2;

        ByteList header = encodeHeader(0, 0, questionCount, 0, 0, 0);

        List<Integer> q1Name = List.of(
                3, (int) 'a',(int) 'b', (int) 'c',
                3, (int) 'c', (int) 'o', (int) 'm',
                0);
        ByteList question1 = questionWithNameFromBytes(q1Name, 12, 34);
        int firstNameLocation = header.size();
        ByteList firstNamePointer = Decoder.pointerTo(firstNameLocation);
        ByteList question2 = firstNamePointer.append(new ByteList(List.of(0x10, 0x23, 0x45, 0x67)));

        ByteList message = header.append(question1, question2);
        Decoder decoder = new Decoder(message);

        // Exercise

        ParseResult<List<Question>> listParseResult = decoder.getQuestions();
        List<Question> questions = listParseResult.getResult();

        // Verify
        assertEquals(2, questions.size());
        Question secondQuestion = questions.get(1);
        String secondName = secondQuestion.getName().toString();
        assertEquals("abc.com", secondName);
        assertEquals(0x1023, secondQuestion.getType());
        assertEquals(0x4567, secondQuestion.getQClass());
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


    @Test
    void instanceFromByteList() {

        // Setup
        String clearText = "abc.def";
        ByteList byteList = encodedText(clearText);
        Decoder decoder = new Decoder(byteList);

        // Exercise
        Name dn = decoder.nameFrom(0);

        // Verify
        assertEquals(clearText, dn.toString());
    }

    @Test
    void instanceFromByteListStartingMidway() {

        // Setup
        String clearText = "abc.def";
        ByteList containsTheName = encodedText(clearText);

        ByteList ignoredPrefix = encodedText("to be ignored");
        ByteList wholeList = ByteList.concatLists(ignoredPrefix, containsTheName);

        int startingPoint = ignoredPrefix.size();
        Decoder decoder = new Decoder(wholeList);

        // Exercise
        Name dn = decoder.nameFrom(startingPoint);

        // Verify
        assertEquals(clearText, dn.toString());
    }

    @Test
    void twoNamesAndAPointer() {

        // Setup
        String name1 = "name1";
        String name2 = "secondName";
        ByteList encodedName1 = encodedText(name1);
        ByteList encodedName2 = encodedText(name2);
        // Define a pointer to name2. It starts at the index that is the size of name1.
        var pointTo = encodedName1.size();
        int startingPoint = encodedName1.size() + encodedName2.size();

        ByteList pointerList = Decoder.pointerTo(pointTo);
        ByteList wholeList = ByteList.concatLists(encodedName1, encodedName2, pointerList);
        Decoder decoder = new Decoder(wholeList);

        // Exercise
        Name name = decoder.nameFrom(startingPoint);

        // Verify
        assertEquals(name2, name.toString());
        // Since nothing follows the pointer, we know that only the
        // length of the pointer (2 bytes) is consumed.

    }

    private ByteList encodedText(String clearText) {
        Name name = new Name(clearText);
        String hex = name.asList().hex();
        return ByteList.of(hex);
    }

    @Test
    void pointerArithmetic() {

        // Exercise
        ByteList pair = Decoder.pointerTo(0x0105);

        // Verify
        assertEquals(2, pair.size());
        Integer msb = pair.get(0);
        Integer lsb = pair.get(1);
        assertEquals(0xc1, msb);
        assertEquals(0x05, lsb);
    }

    @Test
    void noQuestionsNoAnswers() {

        // Setup
        ByteList wholeList = encodeHeader(0x12ab, 0, 0, 0, 0, 0);
        Decoder decoder = new Decoder(wholeList);

        // Exercise
        List<ResourceRecord> answers = decoder.getAnswers();

        // Verify
        assertEquals(0, answers.size());
    }

    @Test
    void oneAnswerNoQuestions() {
        // Setup

        String domainName = "abc.com";
        int recordType = 0x4321;
        int dataClass = 0x1002;
        int timeToLive = 0x1003;
        List<Integer> data = List.of(1, 2, 3, 5, 8);

        Decoder decoder = decoderWith(domainName, recordType, dataClass, timeToLive, data);

        // Exercise
        List<ResourceRecord> answers = decoder.getAnswers();

        // Verify
        assertEquals(1, answers.size());
    }

    @Test
    void recordNameOneAnswerNoQuestions() {
        // Setup

        String domainName = "abc.com";
        int recordType = 0;
        int dataClass = 0;
        int timeToLive = 0;
        List<Integer> data = List.of();

        Decoder decoder = decoderWith(domainName, recordType, dataClass, timeToLive, data);

        // Exercise
        ResourceRecord resourceRecord = decoder.getAnswers().get(0);

        // Verify
        assertEquals(domainName, resourceRecord.getNameString());
    }

    @Test
    @Disabled("do noQuestionsNoAnswers first")
    void recordTypeOneAnswerNoQuestions() {
        // Setup

        String domainName = "ignored";
        int recordType = 0x4321;
        int dataClass = 0;
        int timeToLive = 0;
        List<Integer> data = List.of();

        Decoder decoder = decoderWith(domainName, recordType, dataClass, timeToLive, data);

        // Exercise
        ResourceRecord resourceRecord = decoder.getAnswers().get(0);

        // Verify
        assertEquals(recordType, resourceRecord.getType());
    }

    @Test
    @Disabled("do noQuestionsNoAnswers first")
    void recordClassOneAnswerNoQuestions() {
        // Setup

        String domainName = "abc.com";
        int recordType = 0x4321;
        int dataClass = 0x1002;
        int timeToLive = 0x1003;
        List<Integer> data = List.of(1, 2, 3, 5, 8);

        Decoder decoder = decoderWith(domainName, recordType, dataClass, timeToLive, data);

        // Exercise
        ResourceRecord resourceRecord = decoder.getAnswers().get(0);

        // Verify
        assertEquals(dataClass, resourceRecord.getRDataClass());
    }

    @Test
    @Disabled("do noQuestionsNoAnswers first")
    void recordTtlOneAnswerNoQuestions() {
        // Setup

        String domainName = "ignored";
        int recordType = 0;
        int dataClass = 0;
        int timeToLive = 0x1003;
        List<Integer> data = List.of(1, 2, 3, 5, 8);

        Decoder decoder = decoderWith(domainName, recordType, dataClass, timeToLive, data);

        // Exercise
        ResourceRecord resourceRecord = decoder.getAnswers().get(0);

        // Verify
        assertEquals(timeToLive, resourceRecord.getTimeToLive());
    }

    @Test
    @Disabled("do noQuestionsNoAnswers first")
    void recordLengthOneAnswerNoQuestions() {
        // Setup

        String domainName = "ignored";
        int recordType = 0;
        int dataClass = 0;
        int timeToLive = 0;
        List<Integer> data = List.of(1, 2, 3, 5);

        Decoder decoder = decoderWith(domainName, recordType, dataClass, timeToLive, data);

        // Exercise
        ResourceRecord resourceRecord = decoder.getAnswers().get(0);

        // Verify
        assertEquals(data.size(), resourceRecord.getRdLength());
        assertEquals(new ByteList(data), resourceRecord.getRData());
    }

    @Test
    @Disabled("do noQuestionsNoAnswers first")
    void recordDataOneAnswerNoQuestions() {
        // Setup

        String domainName = "ignored";
        int recordType = 0;
        int dataClass = 0;
        int timeToLive = 0;
        List<Integer> data = List.of(1, 2, 3, 5, 8);

        Decoder decoder = decoderWith(domainName, recordType, dataClass, timeToLive, data);

        // Exercise
        ResourceRecord resourceRecord = decoder.getAnswers().get(0);

        // Verify
        assertEquals(new ByteList(data), resourceRecord.getRData());
    }

    private Decoder decoderWith(String domainName, int recordType, int dataClass, int timeToLive, List<Integer> data) {
        ByteList encodedName = encodedText(domainName);
        ByteList encodedAnswer = encodedName
                .append(ByteList.fromInt(recordType))
                .append(ByteList.fromInt(dataClass))
                .append(ByteList.fromInt(timeToLive))
                .append(ByteList.fromInt(data.size()))
                .append(new ByteList(data))
                .append(new ByteList())
                ;

        ByteList header = encodeHeader(0x12ab, 0, 0, 1, 0, 0);
        ByteList wholeList = header.append(encodedAnswer);

        return new Decoder(wholeList);
    }
}
