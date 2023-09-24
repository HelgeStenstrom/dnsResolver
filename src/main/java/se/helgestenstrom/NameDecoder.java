package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Decodes {@link ByteList}s into instances of {@link Name}.
 */
public class NameDecoder {


    /**
     * @param encoded    A message to be parsed
     * @param startIndex The position in the message, where to start.
     * @return a Name and a start position for continued parsing
     */
    public ParseResult<Name> nameAndNext(ByteList encoded, int startIndex) {
        Optional<Integer> maybePointer = encoded.pointerValue(startIndex);
        if (maybePointer.isPresent()) {
            Name name = nameFrom(encoded, maybePointer.get());
            int consumed = 2;
            return new ParseResult<>(name, startIndex + consumed);
        } else {
            Name name = nameFrom(encoded, startIndex);
            int consumedByLabels = name.labels().stream().mapToInt(String::length).sum();
            int consumedLengthPrefix = name.labels().size() ;
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
    private Name nameFrom(ByteList byteList, int offset) {
        var isPointer = byteList.pointerValue(offset);
        if (isPointer.isPresent()) {
            return nameFrom(byteList, isPointer.get());
        }
        var partial = byteList.subList(offset, byteList.size());
        return nameFrom(partial);
    }

    private Name nameFrom(ByteList bytes) {
        var pointer = 0;

        int partLength = bytes.get(pointer);
        List<String> collector = new ArrayList<>();
        while (partLength != 0) {

            var piece = bytes.subList(pointer + 1, pointer + 1 + partLength);
            var collect = piece.stream()
                    .map(c -> (char) c.intValue())
                    .map(Object::toString)
                    .collect(Collectors.joining());

            collector.add(collect);
            pointer += partLength + 1;
            partLength = bytes.get(pointer);
        }

        String collect = String.join(".", collector);

        return new Name(collect);
    }
}
