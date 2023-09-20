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

    /**
     * @param startingPoint offset from start of whole message
     * @return an instance of {@link Name}
     */
    public Name nameFrom(int startingPoint) {
        return nameDecoder.nameFrom(encoded, startingPoint);
    }

    public List<ResourceRecord> getAnswers() {
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
        int anCount = getHeader().getAnCount();
        int nsCount = getHeader().getNsCount();

        ParseResult<List<Question>> questions = getQuestions();
        ArrayList<ParseResult<ResourceRecord>> answers = getResourceRecords(anCount, questions.getNextIndex());
        var nsStartIndex = answers.stream()
                .reduce((first, second) -> second).orElseThrow().getNextIndex();
        ArrayList<ParseResult<ResourceRecord>> nsRecords = getResourceRecords(nsCount, nsStartIndex);
        return nsRecords.stream().map(ParseResult::getResult).toList();
    }


}
