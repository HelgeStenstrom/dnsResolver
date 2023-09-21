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


    private List<ResourceRecord> getAnswers(int anCount, int startIndex) {

        return getRecordsResults(anCount, startIndex)
                .stream()
                .map(ParseResult::getResult)
                .toList();
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

    private List<ResourceRecord> getNameServerResources(int qdCount, int anCount, int nsCount) {
        int nextIndex = getQuestions(qdCount).getNextIndex();
        ArrayList<ParseResult<ResourceRecord>> nsRecords = getRecordsResults(
                nsCount,
                getRecordsResults(anCount, nextIndex).stream()
                        .reduce((first, second) -> second)
                        .orElse(new ParseResult<>(null, nextIndex))
                        .getNextIndex());

        return nsRecords.stream()
                .map(ParseResult::getResult)
                .toList();
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

    private List<ResourceRecord> getAdditionalRecords() {
        int nextIndex = getRecordsResults(getHeader().getNsCount(), getRecordsResults(getHeader().getAnCount(), getQuestions(getHeader().getQdCount()).getNextIndex()).stream()
                .reduce((first, second) -> second)
                .orElse(new ParseResult<>(null, getQuestions(getHeader().getQdCount()).getNextIndex()))
                .getNextIndex()).stream()
                .reduce((first, second) -> second)
                .orElse(new ParseResult<>(null, 0))
                .getNextIndex();
        ArrayList<ParseResult<ResourceRecord>> resourceRecords = getRecordsResults(getHeader().getArCount(), nextIndex);
        return resourceRecords.stream()
                .map(ParseResult::getResult)
                .toList();
    }

    public DnsMessage getDnsMessage() {
        Header header = getHeader();
        int qdCount = header.getQdCount();
        int anCount = header.getAnCount();
        int nsCount = header.getNsCount();
        ParseResult<List<Question>> questions = getQuestions(qdCount);
        int answersStartIndex = questions.getNextIndex();
        List<ResourceRecord> answers = getAnswers(anCount, answersStartIndex);
        List<ResourceRecord> nameServerResources = getNameServerResources(qdCount, anCount, nsCount);
        List<ResourceRecord> additionalRecords = getAdditionalRecords();
        return new DnsMessage(header, questions.getResult(), answers, nameServerResources, additionalRecords);
    }
}
