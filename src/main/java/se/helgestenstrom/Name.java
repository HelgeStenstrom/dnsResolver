package se.helgestenstrom;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds the QNAME from RFC 1035, section 4.1.2
 */
public class Name {
    private final List<String> labels;

    /**
     * @param value The domain name, including separator dots.
     */
    public Name(String value) {
        this.labels = labels(value);
    }

    /**
     * @param name full name including periods, to be converted
     * @return a list of the words between the dots in the name.
     */
    private List<String> labels(String name) {
        if (name.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(name.split("\\.")).toList();
    }

    /**
     * @param labels the sub-strings of a name.
     */
    public Name(List<String> labels) {
        this.labels = labels;
    }

    /**
     * @return the name as a list
     */
    public ByteList asList() {
        return ByteList.of(hex());
    }

    @Override
    public String toString() {
        return String.join(".", labels);
    }

    private String partHex(String s) {
        String preamble = String.format("%02x", s.length());
        String encoded = s.chars().mapToObj(c -> String.format("%02x", c)).collect(Collectors.joining());
        return preamble + encoded;
    }

    private String hex() {

        String collect = labels.stream().map(this::partHex).collect(Collectors.joining());
        return collect + "00";
    }

    /**
     * @return a list of the words between the dots in the name.
     */
    public List<String> getLabels() {
        return labels;
    }

}
