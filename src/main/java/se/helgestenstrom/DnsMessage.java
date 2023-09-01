package se.helgestenstrom;

import java.util.HexFormat;
import java.util.List;

/**
 * A DNS message, as described by RFC 1035, section 4, and as required by this exercise. (See the README.md file)
 */
public class DnsMessage {


    private final Header header;
    private final List<Question> questions;

    /**
     * @param header The Header of RFC 1035, section 4.1
     * @param questions zero or more Questions of RFC 1035, section 4.1
     */
    public DnsMessage(Header header, List<Question> questions) {
        this.header = header;
        this.questions = questions;
    }



    /**
     * @return The DNS message as a hex string, representing bytes to be sent
     */
    public String hex() {

        return header.hex() + questions.get(0).hex();
    }

    /**
     * @return byte array corresponding to the DnsMessage
     */
    public byte[] bytes() {
        var toBeConvertedToByteArray = hex();

        return HexFormat.of().parseHex(toBeConvertedToByteArray);


    }
}
