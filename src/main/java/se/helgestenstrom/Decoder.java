package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

        int sizeOfHeader = 12;
        Optional<Integer> maybePointer = encoded.pointerValue(sizeOfHeader);
        if (maybePointer.isPresent()) {
            throw new UnsupportedOperationException("Not implemented yet");
        }
        var bytes = encoded.subList(sizeOfHeader, encoded.size());

        int idx = 0;
        int labelLength = bytes.get(idx);
        ArrayList<String> labels = new ArrayList<>();
        while (labelLength != 0) {
            var piece = bytes.subList(idx + 1, idx + 1 + labelLength);
            var label = piece.stream()
                    .map(c -> (char) c.intValue())
                    .map(Object::toString)
                    .collect(Collectors.joining());
            labels.add(label);
            idx += labelLength + 1;
            labelLength = bytes.get(idx);
        }


        return List.of(new Question(labels, "HardcodedName", "1234", "5678"));
    }

    /**
     * @param startingPoint offset from start of whole message
     * @return an instance of {@link Name}
     */
    public Name nameFrom(int startingPoint) {
        return nameDecoder.nameFrom(encoded, startingPoint);
    }
}
