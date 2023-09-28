package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Decodes {@link ByteList}s into instances of {@link Name}.
 */
public class NameDecoder {


    private final ByteList encoded;

    /**
     * @param encoded The complete message that contains names to be decoded (and other stuff)
     */
    public NameDecoder(ByteList encoded) {
        this.encoded = encoded;
    }

    /**
     * @param startIndex The position in the message, where to start.
     * @return a Name and a start position for continued parsing
     */
    public ParseResult<Name> nameAndNext(int startIndex) {
        Integer firstByte = encoded.get(startIndex);
        boolean isCount = (1 <= firstByte && firstByte <= 63);
        boolean isEndIndicator = firstByte == 0;
        boolean isPointerFirstByte = firstByte >= 192;

        if (isCount) {
            // nothing yet
        }

        Optional<Integer> maybePointer = encoded.pointerValue(startIndex);
        if (maybePointer.isPresent()) {
            Name name = nameFrom(maybePointer.get());
            int consumed = 2;
            return new ParseResult<>(name, startIndex + consumed);
        } else {
            Name name = nameFrom(startIndex);
            int consumedByLabels = name.labels().stream().mapToInt(String::length).sum();
            int consumedLengthPrefix = name.labels().size();
            int consumedByEndingZero = 1;
            int consumed = consumedByLabels + consumedLengthPrefix + consumedByEndingZero;
            return new ParseResult<>(name, startIndex + consumed);
        }
    }

    /**
     * @param byteList Sequence to be decoded into a {@link Name}
     * @param offset   Point in the sequence from which to start the decoding
     * @return an instance of {@link Name}
     */
    private Name nameFrom(int offset) {
        ByteList byteList = encoded;
        var isPointer = byteList.pointerValue(offset);
        if (isPointer.isPresent()) {
            return nameFrom(isPointer.get());
        }

        var index = offset;

        int partLength = byteList.get(index);
        List<String> collector = new ArrayList<>();
        while (partLength != 0 && byteList.pointerValue(index).isEmpty()) {

            var piece = byteList.subList(index + 1, index + 1 + partLength);
            var collect = piece.stream()
                    .map(c -> (char) c.intValue())
                    .map(Object::toString)
                    .collect(Collectors.joining());

            collector.add(collect);
            index += partLength + 1;
            partLength = byteList.get(index);
        }

        String collect = String.join(".", collector);

        return new Name(collect);
    }

}
