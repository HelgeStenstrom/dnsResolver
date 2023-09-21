package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;

/**
 * Decodes a message and creates various items of domain types
 */
public class Decoder {
    private final ByteList encoded;
    private final NameDecoder nameDecoder;

    /**
     * Decodes a message and creates various items of domain types
     *
     * @param encoded message
     */
    public Decoder(ByteList encoded) {
        this.encoded = encoded;

        nameDecoder = new NameDecoder();
    }

    /**
     * Creates a 2-byte pointer, to be used in names
     *
     * @param value offset to point to
     * @return a 2-byte list.
     */
    public static ByteList pointerTo(int value) {
        int msb = (value & 0xff00) >> 8;
        int lsb = value & 0xff;
        int pointerMarker = 0xc0;

        return new ByteList(List.of(msb | pointerMarker, lsb));
    }

    public int getId() {
        return encoded.u16(0);
    }

    public Header getHeader() {
        Id id = new Id(getId());
        int flags = encoded.u16(2);
        int qdCount = encoded.u16(4);
        int anCount = encoded.u16(6);
        int nsCount = encoded.u16(8);
        int arCount = encoded.u16(10);
        return new Header(id, new Flags(flags), qdCount, anCount, nsCount, arCount);
    }

    public ParseResult<List<Question>> getQuestions() {
        int qdCount = getHeader().getQdCount();

        int startingPoint = 12;

        return parseQuestions(qdCount, startingPoint, nameDecoder, encoded);
    }

    private ParseResult<List<Question>> parseQuestions(int qdCount, final int startingPoint, NameDecoder nameDecoder, ByteList encoded) {
        List<Question> collector = new ArrayList<>();

        int nextIndex = startingPoint;
        for (int i = 0; i < qdCount; i++) {

            ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(encoded, nextIndex);

            int nextIndex1 = nameParseResult.getNextIndex();
            int qt = encoded.u16(nextIndex1);
            int qc = encoded.u16(nextIndex1 + 2);
            Question question = new Question(nameParseResult.getResult(), qt, qc);
            collector.add(question);
            nextIndex = nextIndex1 + 4;
        }
        return new ParseResult<>(collector, nextIndex);
    }


    public List<ResourceRecord> getAnswers() {
        // TODO: Make this method a wrapper over getDnsMessage.getAnswers()

        // TODO: Find why getDnsMessage has some side effect.

        //getNameServerResults();


        int anCount = getHeader().getAnCount();

        ParseResult<List<Question>> questions = getQuestions();

        return getResourceRecords(anCount, questions.getNextIndex())
                .stream()
                .map(ParseResult::getResult)
                .toList();
    }

    private List<ResourceRecord> getAnswers2() {
        int anCount = getHeader().getAnCount();

        ParseResult<List<Question>> questions = getQuestions();

        return getResourceRecords(anCount, questions.getNextIndex())
                .stream()
                .map(ParseResult::getResult)
                .toList();
    }

    private ArrayList<ParseResult<ResourceRecord>> getResourceRecords(int anCount, int startIndex) {
        ArrayList<ParseResult<ResourceRecord>> collector = new ArrayList<>();
        for (int i = 0; i < anCount; i++) {
            ParseResult<ResourceRecord> resourceRecordParseResult = getOneRecord(startIndex);
            collector.add(resourceRecordParseResult);
            startIndex = resourceRecordParseResult.getNextIndex();
        }
        return collector;
    }

    private ParseResult<ResourceRecord> getOneRecord(int nextIndex) {
        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(encoded, nextIndex);
        Name name =  nameParseResult.getResult();
        int type = encoded.u16(nameParseResult.getNextIndex());
        int rDataClass = encoded.u16(nameParseResult.getNextIndex() + 2);
        int timeToLive = encoded.u16(nameParseResult.getNextIndex() + 4);
        int rdLength = encoded.u16(nameParseResult.getNextIndex() + 6);
        int rdIndex = nameParseResult.getNextIndex() + 8;
        ByteList rData = encoded.subList(rdIndex, rdIndex + rdLength);
        ResourceRecord oneAnswer = new ResourceRecord(name, type, rDataClass, timeToLive, rData);
        return new ParseResult<>(oneAnswer, rdIndex + rdLength);
    }

    public List<ResourceRecord> getNameServerResources() {
        return getDnsMessage().getNameServerResources();
    }
    private List<ResourceRecord> getNameServerResources2() {
        ArrayList<ParseResult<ResourceRecord>> nsRecords = getNameServerResults();

        return nsRecords.stream()
                .map(ParseResult::getResult)
                .toList();
    }

    private ArrayList<ParseResult<ResourceRecord>> getNameServerResults() {
        int anCount = getHeader().getAnCount();
        int nsCount = getHeader().getNsCount();

        ParseResult<List<Question>> questions = getQuestions();
        ArrayList<ParseResult<ResourceRecord>> answers = getResourceRecords(anCount, questions.getNextIndex());

        int nsStartIndex = answers.stream()
                .reduce((first, second) -> second)
                .orElseThrow()
                .getNextIndex();

        return getResourceRecords(nsCount, nsStartIndex);
    }


    public List<ResourceRecord> getAdditionalRecords() {
        return getDnsMessage().getAdditionalRecords();
    }


    private List<ResourceRecord> getAdditionalRecords2() {
        int nextIndex = getNameServerResults().stream()
                .reduce((first, second) -> second)
                .orElseThrow()
                .getNextIndex();
        ArrayList<ParseResult<ResourceRecord>> resourceRecords = getResourceRecords(getHeader().getArCount(), nextIndex);
        return resourceRecords.stream()
                .map(ParseResult::getResult)
                .toList();
    }

    public DnsMessage getDnsMessage() {
        Header header = getHeader();
        List<Question> questions = getQuestions().getResult();
        List<ResourceRecord> answers = getAnswers2();
        List<ResourceRecord> nameServerResources = getNameServerResources2();
        List<ResourceRecord> additionalRecords = getAdditionalRecords2();
        return new DnsMessage(header, questions, answers, nameServerResources, additionalRecords);
    }
}
