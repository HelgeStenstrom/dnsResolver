package se.helgestenstrom;

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
     * @return a list representing the message. This list can also be parsed into a message.
     */
    public ByteList byteList() {

        ByteList allQuestions = new ByteList();
        questions.forEach(q -> allQuestions.addAll(q.asList()));
        return header.asList().append(allQuestions);
    }

    /**
     * @return byte array corresponding to the DnsMessage
     */
    public byte[] bytes() {

        return byteList().asArray();
    }
}
