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

    public List<Question> getQuestions() {

        int qdCount = getHeader().getQdCount();

        int startingPoint = 12;

        return parseQuestions(qdCount, startingPoint, nameDecoder, encoded);
    }

    private List<Question> parseQuestions(int qdCount, final int startingPoint, NameDecoder nameDecoder, ByteList encoded) {
        List<Question> collector = new ArrayList<>();

        int nextIndex = startingPoint;
        for (int i = 0; i < qdCount; i++) {
            Pair<Name, Integer> result = nameDecoder.nameAndConsumes(encoded, startingPoint);
            Name name = result.first;

            int qt = encoded.u16(nextIndex + result.second);
            int qc = encoded.u16(nextIndex + result.second+2);
            Question question = new Question(name, qt, qc);
            collector.add(question);
            nextIndex += result.second;
        }
        return collector;
    }

    /**
     * @param startingPoint offset from start of whole message
     * @return an instance of {@link Name}
     */
    public Name nameFrom(int startingPoint) {
        return nameDecoder.nameFrom(encoded, startingPoint);
    }


    /**
     * @param <T1> type of the first value
     * @param <T2> type of the second value
     */
    public static class Pair<T1, T2> {
        private final T1 first;
        private final T2 second;

        /**
         * @param first value
         * @param second value
         */
        public Pair(T1 first, T2 second) {
            this.first = first;
            this.second = second;
        }
    }
}
