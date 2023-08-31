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
     * @param hexEncoded String of hex characters that encodes a DnsMessage, as described in RFC 1035
     * @return a {@link DnsMessage} created from the input
     * @deprecated because DnsMessage should not handle decoding.
     */
    @Deprecated(forRemoval = true)
    public static DnsMessage from(String hexEncoded) {
        Header header = Header.fromHex(hexEncoded.substring(0, 24));
        String restOfHexEncoded = hexEncoded.substring(24);
        Question question = Question.of(restOfHexEncoded);
        return new DnsMessage(header, List.of(question));
    }


    /**
     * @param bytes encodes a DnsMessage, as described in RFC 1035
     * @return a {@link DnsMessage} created from the input
     */
    public static DnsMessage from(byte[] bytes) {
        int numId = bytes[0] << 8 | (bytes[1] & 0xff);
        final Id id = new Id(numId);
        final Flags flags = new Flags(true);
        final Question question = new Question("example.com", "0001", "0001", List.of("example.com"));
        return new DnsMessage(new Header(id, flags, 1, 20, 21, 22), List.of(question));
    }

    public Header getHeader() {
        return header;
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

    /**
     * @return The numerical ID of the instance.
     */
    public int id() {
        return header.getId().id();
    }

    /**
     * @return list of the Questions in the DnsMessage
     */
    public List<Question> getQuestions() {
        return questions;
    }
}
