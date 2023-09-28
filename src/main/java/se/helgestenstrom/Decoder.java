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

        nameDecoder = new NameDecoder(encoded);
    }

    public DnsMessage getDnsMessage() {

        Header header = getHeader();
        int qdCount = header.getQdCount();
        int anCount = header.getAnCount();
        int nsCount = header.getNsCount();
        int arCount = getHeader().getArCount();

        ParseResult<List<Question>> questions = getQuestions(qdCount);
        int answersStartIndex = questions.getNextIndex();

        ArrayList<ParseResult<ResourceRecord>> answersResults = getRecordsResults(anCount, answersStartIndex);
        List<ResourceRecord> answers = getResults(answersResults);
        int nsStartIndex = nextStartIndex(answersResults, answersStartIndex);
        List<ResourceRecord> nameServerResources = getAdditionalRecords(nsCount, nsStartIndex);
        ArrayList<ParseResult<ResourceRecord>> nsResults = getRecordsResults(nsCount, nsStartIndex);
        int additionalStartIndex = nextStartIndex(nsResults, nsStartIndex);
        List<ResourceRecord> additionalRecords = getAdditionalRecords(arCount, additionalStartIndex);
        return new DnsMessage(header, questions.getResult(), answers, nameServerResources, additionalRecords);
    }

    public Header getHeader() {
        Id id = new Id(encoded.u16(0));
        int flags = encoded.u16(2);
        int qdCount = encoded.u16(4);
        int anCount = encoded.u16(6);
        int nsCount = encoded.u16(8);
        int arCount = encoded.u16(10);
        return new Header(id, new Flags(flags), qdCount, anCount, nsCount, arCount);
    }

    private ParseResult<List<Question>> getQuestions(int qdCount) {

        int startingPoint = 12;

        return parseQuestions(qdCount, startingPoint, nameDecoder, encoded);
    }

    private ArrayList<ParseResult<ResourceRecord>> getRecordsResults(int count, int startIndex) {

        ArrayList<ParseResult<ResourceRecord>> collector = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ParseResult<ResourceRecord> resourceRecordParseResult = getOneRecord(startIndex);
            collector.add(resourceRecordParseResult);
            startIndex = resourceRecordParseResult.getNextIndex();
        }
        return collector;
    }

    private List<ResourceRecord> getResults(ArrayList<ParseResult<ResourceRecord>> recordsResults) {

        return recordsResults
                .stream()
                .map(ParseResult::getResult)
                .toList();
    }

    private int nextStartIndex(ArrayList<ParseResult<ResourceRecord>> nsResults, int defaultIndex) {
        return nsResults.stream()
                .reduce((first, second) -> second)
                .orElse(new ParseResult<>(null, defaultIndex))
                .getNextIndex();
    }

    private List<ResourceRecord> getAdditionalRecords(int arCount, int additionalStartIndex) {
        return getResources(arCount, additionalStartIndex);
    }

    private ParseResult<List<Question>> parseQuestions(int qdCount, final int startingPoint, NameDecoder nameDecoder, ByteList encoded) {
        List<Question> collector = new ArrayList<>();

        int nextIndex = startingPoint;
        for (int i = 0; i < qdCount; i++) {

            ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(nextIndex);

            int nextIndex1 = nameParseResult.getNextIndex();
            int qt = encoded.u16(nextIndex1);
            int qc = encoded.u16(nextIndex1 + 2);
            Question question = new Question(nameParseResult.getResult(), qt, qc);
            collector.add(question);
            nextIndex = nextIndex1 + 4;
        }
        return new ParseResult<>(collector, nextIndex);
    }

    private ParseResult<ResourceRecord> getOneRecord(int nextIndex) {
        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(nextIndex);
        Name name = nameParseResult.getResult();
        int nextIndex1 = nameParseResult.getNextIndex();
        int type = encoded.u16(nextIndex1);
        int rDataClass = encoded.u16(nextIndex1 + 2);
        long timeToLive = encoded.u32(nextIndex1 + 4);
        int rdLength = encoded.u16(nextIndex1 + 8);
        int rdIndex = nextIndex1 + 10;
        ByteList rData = encoded.subList(rdIndex, rdIndex + rdLength);
        ResourceRecord oneAnswer = new ResourceRecord(name, type, rDataClass, timeToLive, rData);
        return new ParseResult<>(oneAnswer, rdIndex + rdLength);
    }

    private List<ResourceRecord> getResources(int count, int startIndex) {
        ArrayList<ParseResult<ResourceRecord>> records = getRecordsResults(
                count,
                startIndex);

        return getResults(records);
    }
}
