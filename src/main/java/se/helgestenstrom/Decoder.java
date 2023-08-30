package se.helgestenstrom;

/**
 * Decodes a message and creates various items of domain types
 */
public class Decoder {
    private final ByteList encoded;

    /**
     * Decodes a message and creates various items of domain types
     * @param encoded message
     */
    public Decoder(ByteList encoded) {
        this.encoded = encoded;
    }

    public int getId() {
        return encoded.u16(0);
    }
}
