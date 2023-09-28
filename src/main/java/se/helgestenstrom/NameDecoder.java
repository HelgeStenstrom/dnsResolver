package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;
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
            ParseResult<String> label = labelFrom(startIndex);
            ParseResult<Name> rest = nameAndNext(label.getNextIndex());
            List<String> followingLabels = rest.getResult().labels();
            String thisLabel = label.getResult();
            List<String> allLabels = new ArrayList<>();
            allLabels.add(thisLabel);
            allLabels.addAll(followingLabels);

            String nameString = String.join(".", allLabels);

            return new ParseResult<>(new Name(nameString), rest.getNextIndex());
        } else if (isEndIndicator) {
            return new ParseResult<>(new Name(""), startIndex + 1);
        } else if (isPointerFirstByte) {
            int nextIndex = startIndex + 2; // The pointer takes 2 bytes
            int pointsTo = encoded.pointerValue(startIndex).orElseThrow();
            ParseResult<Name> followingResult = nameAndNext(pointsTo);
            return new ParseResult<>(followingResult.getResult(), nextIndex);
        } else throw new IllegalArgumentException("Programming error, or illegal character.");


    }

    private ParseResult<String> labelFrom(int startIndex) {
        Integer count = encoded.get(startIndex);
        ByteList subList = encoded.subList(startIndex + 1, startIndex + count + 1);
        String collect = subList.stream()
                .map(c -> (char) c.intValue())
                .map(Object::toString)
                .collect(Collectors.joining());
        return new ParseResult<>(collect, startIndex + count + 1);
    }

}
