package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Decodes {@link ByteList}s into instances of {@link Name}.
 */
public class NameDecoder {


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

    /**
     * @param byteList Sequence to be decoded into a {@link Name}
     * @param offset   Point in the sequence from which to start the decoding
     * @return an instance of {@link Name}
     */
    public Name nameFrom(ByteList byteList, int offset) {
        var isPointer = byteList.pointerValue(offset);
        if (isPointer.isPresent()) {
            return nameFrom(byteList, isPointer.get());
        }
        var partial = byteList.subList(offset, byteList.size());
        return nameFrom(partial);
    }

    /**
     * @param encoded full list of the encoded message
     * @param startingPoint where the name starts in the encoded message
     * @return pair of {@link Name} and number of consumed bytes for the name
     */
    public Decoder.Pair<Name, Integer> nameAndConsumes(ByteList encoded, int startingPoint) {
        Optional<Integer> maybePointer = encoded.pointerValue(startingPoint);
        if (maybePointer.isPresent()) {
            Name name = nameFrom(encoded, maybePointer.get());
            int consumed = 2;
            return new Decoder.Pair<>(name, consumed);
        }
        Name name = nameFrom(encoded, startingPoint);
        int sum = name.labels().stream().mapToInt(l -> l.length() + 1).sum();
        return new Decoder.Pair<>(name, sum + 1);
    }

    ParseResult<Name> nameAndNext(ByteList encoded, int nextIndex) {
        Decoder.Pair<Name, Integer> result = nameAndConsumes(encoded, nextIndex);
        return new ParseResult<>(result.getFirst(), nextIndex + result.getSecond());
    }
}
