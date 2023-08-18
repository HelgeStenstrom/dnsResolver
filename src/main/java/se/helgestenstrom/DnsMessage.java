package se.helgestenstrom;

import java.util.HexFormat;

/**
 * A DNS message, as described by RFC 1035, section 4, and as required by this exercise. (See the README.md file)
 */
public class DnsMessage {


    private final Header header;
    private final Question question;


    /**
     * @param header The Header of RFC 1035, section 4.1
     * @param question The Question of RFC 1035, section 4.1
     */
    public DnsMessage(Header header, Question question) {
        this.header = header;
        this.question = question;
    }

    /**
     * @param hexEncoded String of hex characters that encodes a DnsMessage, as described in RFC 1035
     * @return a {@link DnsMessage} created from the input
     */
    public static DnsMessage from(String hexEncoded) {
        String idPart = hexEncoded.substring(0, 4);
        int numId = Integer.parseInt(idPart, 16);
        Id id = new Id(numId);
        final Flags flags = new Flags(true);
        return new DnsMessage(new Header(id, flags), new Question("example.com", "0001", "0001"));
    }


    /**
     * @param bytes encodes a DnsMessage, as described in RFC 1035
     * @return a {@link DnsMessage} created from the input
     */
    public static DnsMessage from(byte[] bytes) {
        int numId = bytes[0] << 8 | (bytes[1] & 0xff);
        final Id id = new Id(numId);
        final Flags flags = new Flags(true);
        return new DnsMessage(new Header(id, flags), new Question("example.com", "0001", "0001"));
    }

    /**
     * @return The DNS message as a hex string, representing bytes to be sent
     */
    public String hex() {

        return header.hex() + question.hex();
    }

    /**
     * @return byte array corresponding to the DnsMessage
     */
    public byte[] bytes() {
        var toBeConvertedToByteArray = hex();

        return HexFormat.of().parseHex(toBeConvertedToByteArray);


    }

    /**
     * @return The numerical ID of the instance.
     */
    public int id() {
        return header.id().id();
    }
}
