package se.helgestenstrom;

import java.util.HexFormat;

/**
 * A DNS message, as described by RFC 1035, section 4, and as required by this exercise. (See the README.md file)
 */
public class DnsMessage {


    private final String domain;
    private final Id id;
    private final Flags flags;


    /**
     * @param id Numerical ID of the message. Any integer in the range 0 to 255.
     * @param flags Represents the second row of the table of RFC 1035, section 4.1.1
     * @param domain for a dns host
     */
    public DnsMessage(Id id, Flags flags, String domain) {
        this.id = id;
        this.flags = flags;
        this.domain = domain;
    }

    public static DnsMessage from(String hexEncoded) {
        String idPart = hexEncoded.substring(0, 4);
        int numId = Integer.parseInt(idPart, 16);
        Id id = new Id(numId);
        return new DnsMessage(id, new Flags(true), "example.com");
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

        return id.hex() + flags.hex()
                + question + answer + authority + additional + new DomainName(domain).hex() + queryType + queryClass;
    }

    /**
     * @return byte array corresponding to the DnsMessage
     */
    public byte[] bytes() {
        var toBeConvertedToByteArray = hex();

        return HexFormat.of().parseHex(toBeConvertedToByteArray);


    }

    public int id() {
        return id.id();
    }
}
