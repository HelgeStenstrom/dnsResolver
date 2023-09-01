package se.helgestenstrom;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Holds the QNAME from RFC 1035, section 4.1.2
 */
public class Name {
    private final String value;

    /**
     * @param value The domain name, including separator dots.
     */
    public Name(String value) {
        this.value = value;
    }


    /**
     * @return the name
     */
    public String getValue() {
        return toString();
    }

    @Override
    public String toString() {
        return value;
    }

    /**
     * @return the name as a list
     */
    public ByteList asList() {
        return ByteList.of(hex());
    }

    private String hex() {

        var parts = value.split("\\.");
        String collect = Arrays.stream(parts).map(this::partHex).collect(Collectors.joining());
        return collect + "00";
    }

    private String partHex(String s) {
        String preamble = String.format("%02x", s.length());
        String encoded = s.chars().mapToObj(c -> String.format("%02x", c)).collect(Collectors.joining());
        return preamble + encoded;
    }

}
