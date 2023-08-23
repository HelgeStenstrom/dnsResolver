package se.helgestenstrom;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

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

        byte[] bytes = HexFormat.of().parseHex(hex);

        var pointer = 0;

        var partLength = bytes[pointer];
        List<String> collector = new ArrayList<>();
        while (partLength != 0) {
            var piece = Arrays.copyOfRange(bytes, pointer+1, pointer + 1 + partLength);
            String s = new String(piece, StandardCharsets.US_ASCII);
            collector.add(s);
            pointer += partLength+1;
            partLength = bytes[pointer];
        }

        String collect = String.join(".", collector);

        return new DomainName(collect);
    }

    /**
     * @param byteList Sequence to be decoded into a {@link DomainName}
     * @param offset Point in the sequence from which to start the decoding
     * @return an instance of {@link DomainName}
     */
    public static DomainName of(ByteList byteList, int offset) {
        var partial = byteList.subList(offset, byteList.size());
        String hex = partial.hex();
        return DomainName.ofHex(hex);
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
