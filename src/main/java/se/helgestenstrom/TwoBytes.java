package se.helgestenstrom;

/**
 * Replacement of String, to represent two bytes (16 bits)
 */
public class TwoBytes implements Hex{

    private final int value;

    private TwoBytes(String hex) {
        assert hex.length() == 4;
        value = Integer.parseInt(hex, 16);
    }

    /**
     * @param hex Four hexadecimal characters
     * @return an instance
     */
    public static TwoBytes of(String hex) {
        return new TwoBytes(hex);
    }

    @Override
    public String hex() {
        return String.format("%04x", value);
    }

    /**
     * @return Integer value of the instance
     */
    public int asInt() {
        return value;
    }
}
