package se.helgestenstrom;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Encoded implements Hex {
    private final String[] parts;

    public Encoded(String host) {

        parts = host.split("\\.");
    }


    @Override
    public String hex() {

        String collect = Arrays.stream(parts)
                .map(this::partHex)
                .collect(Collectors.joining());
        return collect + "00";
    }

    public String partHex(String s) {
        String preamble = String.format("%02x", s.length());
        String encoded = s.chars()
                .mapToObj(c -> String.format("%02x", c))
                .collect(Collectors.joining());
        return preamble + encoded;
    }
}
