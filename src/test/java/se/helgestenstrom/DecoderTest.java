package se.helgestenstrom;

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

        decoder.getDnsMessage();

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
        ByteList encodedAnswer = encodeAnswer(domainName, recordType, dataClass, timeToLive, data);

        ByteList header = encodeHeader(0x12ab, 0, 0, 1, 0, 0);
        ByteList wholeList = header.append(encodedAnswer);

        return new Decoder(wholeList);
    }


    @Test
    void namesOfTwoAnswersNoQuestions() {
        // Setup

        int recordType = 0;
        int dataClass = 0;
        int timeToLive = 0;
        List<Integer> data = List.of();

        ByteList header = encodeHeader(0x12ab, 0, 0, 2, 0, 0);
        ByteList wholeList = header
                .append(encodeAnswer("name1", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("name2", recordType, dataClass, timeToLive, data))
                ;

        Decoder decoder = new Decoder(wholeList);

        // Exercise
        List<ResourceRecord> answers = decoder.getAnswers();


        // Verify
        assertEquals(2, answers.size());
        ResourceRecord resourceRecord1 = answers.get(0);
        ResourceRecord resourceRecord2 = answers.get(1);
        assertEquals("name1", resourceRecord1.getNameString());
        assertEquals("name2", resourceRecord2.getNameString());
    }


    @Test
    void oneAnswerOneNameServerResource() {

        // Setup
        int recordType = 0;
        int dataClass = 0;
        int timeToLive = 0;
        List<Integer> data = List.of();

        ByteList header = encodeHeader(0x12ab, 0, 0, 1, 1, 0);
        ByteList wholeList = header
                .append(encodeAnswer("name1", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("name2", recordType, dataClass, timeToLive, data));

        Decoder decoder = new Decoder(wholeList);

        // Exercise
        List<ResourceRecord> answers = decoder.getAnswers();
        List<ResourceRecord> nsRecords = decoder.getNameServerResources();


        // Verify
        assertEquals(1, answers.size());
        assertEquals(1, nsRecords.size());
        ResourceRecord answerRecord = answers.get(0);
        ResourceRecord nsRecord = nsRecords.get(0);
        assertEquals("name1", answerRecord.getNameString());
        assertEquals("name2", nsRecord.getNameString());
    }

    @Test
    void multipleResourceRecords() {

        // Setup
        int recordType = 0;
        int dataClass = 0;
        int timeToLive = 0;
        List<Integer> data = List.of();

        ByteList header = encodeHeader(0x12ab, 0, 0, 1, 2, 3);
        ByteList wholeList = header
                .append(encodeAnswer("an1", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ns1", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ns2", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ar1", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ar2", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ar3", recordType, dataClass, timeToLive, data))
                ;

        Decoder decoder = new Decoder(wholeList);

        // Exercise
        List<ResourceRecord> answers = decoder.getAnswers();
        List<ResourceRecord> nsRecords = decoder.getNameServerResources();
        List<ResourceRecord> arRecords = decoder.getAdditionalRecords();


        // Verify
        assertEquals(1, answers.size());
        assertEquals(2, nsRecords.size());
        assertEquals(3, arRecords.size());
        ResourceRecord answerRecord = answers.get(0);
        ResourceRecord nsRecord = nsRecords.get(0);
        assertEquals("an1", answerRecord.getNameString());
        assertEquals("ns1", nsRecord.getNameString());
        List<String> arNames = arRecords.stream().map(ResourceRecord::getNameString).toList();
        assertEquals(List.of("ar1", "ar2", "ar3"), arNames);
    }

    @Test
    void completeDnsMessage() {

        // Setup
        int recordType = 0;
        int dataClass = 0;
        int timeToLive = 0;
        List<Integer> data = List.of();

        ByteList encodedHeader = encodeHeader(0x12ab, 0, 1, 1, 2, 3);
        List<Integer> q1Name = List.of(
                3, (int) 'a',(int) 'b', (int) 'c',
                3, (int) 'c', (int) 'o', (int) 'm',
                0);
        ByteList wholeList = encodedHeader
                .append(questionWithNameFromBytes(q1Name, 13, 45))
                .append(encodeAnswer("an1", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ns1", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ns2", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ar1", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ar2", recordType, dataClass, timeToLive, data))
                .append(encodeAnswer("ar3", recordType, dataClass, timeToLive, data))
                ;

        Decoder decoder = new Decoder(wholeList);

        // Exercise
        DnsMessage message = decoder.getDnsMessage();

        // Verify
        Header header = message.getHeader();
        assertEquals(0x12ab, header.getId().id());
        assertEquals(1, message.getQuestions().size());
        Question question = message.getQuestions().get(0);
        String nameString = question.getName().toString();
        assertEquals("abc.com", nameString);

        assertEquals(List.of("an1"), message.getAnswers().stream().map(ResourceRecord::getNameString).toList());
        assertEquals(List.of("ns1", "ns2"), message.getNameServerResources().stream().map(ResourceRecord::getNameString).toList());
        assertEquals(List.of("ar1", "ar2", "ar3"), message.getAdditionalRecords().stream().map(ResourceRecord::getNameString).toList());

    }

    private ByteList encodeAnswer(String domainName, int recordType, int dataClass, int timeToLive, List<Integer> data) {
        ByteList encodedName = encodedText(domainName);
        return encodedName
        .append(ByteList.fromInt(recordType))
        .append(ByteList.fromInt(dataClass))
        .append(ByteList.fromInt(timeToLive))
        .append(ByteList.fromInt(data.size()))
        .append(new ByteList(data))
        .append(new ByteList());
    }
}
