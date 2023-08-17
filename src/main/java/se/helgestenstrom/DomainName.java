package se.helgestenstrom;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Holds the QNAME from RFC 1035, section 4.1.2
 */
public class DomainName implements Hex {
    private final String[] parts;

    /**
     * @param domain The domain name, including separator dots.
     */
    public DomainName(String domain) {

        parts = domain.split("\\.");
    }


    @Override
    public String hex() {

        String collect = Arrays.stream(parts)
                .map(this::partHex)
                .collect(Collectors.joining());
        return collect + "00";
    }

    private String partHex(String s) {
        String preamble = String.format("%02x", s.length());
        String encoded = s.chars()
                .mapToObj(c -> String.format("%02x", c))
                .collect(Collectors.joining());
        return preamble + encoded;
    }
}
