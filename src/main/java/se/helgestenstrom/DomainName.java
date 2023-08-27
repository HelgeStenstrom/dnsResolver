package se.helgestenstrom;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Holds the QNAME from RFC 1035, section 4.1.2
 */
public class DomainName implements Hex {
    private final String name;

    /**
     * @param name The domain name, including separator dots.
     */
    public DomainName(String name) {
        this.name = name;
    }

    /**
     * @param hex Hexadecimal representation to be converted to an instance,
     *            according to the description of QNAME in RFC 1035, section 4.1.2
     * @return an instance
     */
    public static DomainName ofHex(String hex) {

        List<Integer> bytes = toList(HexFormat.of().parseHex(hex));

        return of(bytes);
    }

    static DomainName of(List<Integer> bytes) {
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
            pointer += partLength+1;
            partLength = bytes.get(pointer);
        }

        String collect = String.join(".", collector);

        return new DomainName(collect);
    }

    /**
     * Conversion method suggested by ChatGPT. Arrays.asList() doesn't seem to work in this case.
     * @param bytes The byte array to be converted
     * @return The corresponding list of integers.
     */
    private static List<Integer> toList(byte[] bytes) {

        return IntStream
                .range(0, bytes.length)
                .mapToObj(i -> bytes[i] & 0xFF)
                .toList();
    }

    /**
     * @param byteList Sequence to be decoded into a {@link DomainName}
     * @param offset Point in the sequence from which to start the decoding
     * @return an instance of {@link DomainName}
     */
    public static DomainName of(ByteList byteList, int offset) {
        var isPointer = byteList.pointerValue(offset);
        if (isPointer.isPresent()) {
            return DomainName.of(byteList, isPointer.get());
        }
        var partial = byteList.subList(offset, byteList.size());
        return DomainName.of(partial);
    }

    /**
     * Creates a 2-byte pointer, to be used in names
     * @param value offset to point to
     * @return a 2-byte list.
     */
    public static ByteList pointerTo(int value) {
        int msb = (value & 0xff00) >> 8;
        int lsb = value & 0xff;
        int pointerMarker = 0xc0;

        return new ByteList(List.of(msb | pointerMarker, lsb));
    }

    /**
     * @return the name
     */
    public String getName() {
        return toString();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String hex() {

        var parts = name.split("\\.");
        String collect = Arrays.stream(parts).map(this::partHex).collect(Collectors.joining());
        return collect + "00";
    }

    private String partHex(String s) {
        String preamble = String.format("%02x", s.length());
        String encoded = s.chars().mapToObj(c -> String.format("%02x", c)).collect(Collectors.joining());
        return preamble + encoded;
    }
}
