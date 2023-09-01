package se.helgestenstrom;

import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

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
    private String hex() {

        return header.asList().hex() + questions.stream()
                .map(Question::asList)
                .map(ByteList::hex)
                .collect(Collectors.joining());
    }

    /**
     * @return a list representing the message. This list can also be parsed into a message.
     */
    public ByteList byteList() {

        return  ByteList.of(hex());
    }

    /**
     * @return byte array corresponding to the DnsMessage
     */
    public byte[] bytes() {
        var toBeConvertedToByteArray = hex();

        return HexFormat.of().parseHex(toBeConvertedToByteArray);


    }
}
