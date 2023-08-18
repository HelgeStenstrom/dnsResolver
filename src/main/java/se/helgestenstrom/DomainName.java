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
    public static DomainName of(String hex) {

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
