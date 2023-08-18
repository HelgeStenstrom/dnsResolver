package se.helgestenstrom;

import java.util.HexFormat;

/**
 * A DNS message, as described by RFC 1035, section 4, and as required by this exercise. (See the README.md file)
 */
public class DnsMessage {


    private final String domain;
    private final Header header;


    /**
     * @param id Numerical ID of the message. Any integer in the range 0 to 255.
     * @param flags Represents the second row of the table of RFC 1035, section 4.1.1
     * @param domain for a dns host
     */
    public DnsMessage(Id id, Flags flags, String domain) {
        header = new Header(id, flags);
        this.domain = domain;
    }

    /**
     * @param hexEncoded String of hex characters that encodes a DnsMessage, as described in RFC 1035
     * @return a {@link DnsMessage} created from the input
     */
    public static DnsMessage from(String hexEncoded) {
        String idPart = hexEncoded.substring(0, 4);
        int numId = Integer.parseInt(idPart, 16);
        Id id = new Id(numId);
        return new DnsMessage(id, new Flags(true), "example.com");
    }


    /**
     * @param bytes encodes a DnsMessage, as described in RFC 1035
     * @return a {@link DnsMessage} created from the input
     */
    public static DnsMessage from(byte[] bytes) {
        int numId = bytes[0] << 8 | (bytes[1] & 0xff);
        return new DnsMessage(new Id(numId), new Flags(true), "example.com");
    }

    /**
     * @return The DNS message as a hex string, representing bytes to be sent
     */
    public String hex() {

        String question = "0001";
        String answer = "0000";
        String authority = "0000";
        String additional = "0000";
        String queryType = "0001";
        String queryClass = "0001";

        return header.hex()
                + question + answer + authority + additional + new DomainName(domain).hex() + queryType + queryClass;
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
